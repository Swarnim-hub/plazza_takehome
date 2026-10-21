package com.plazza.ridehailing.service;

import com.plazza.ridehailing.dto.request.BookRideRequest;
import com.plazza.ridehailing.dto.response.RideResponse;
import com.plazza.ridehailing.exception.BadRequestException;
import com.plazza.ridehailing.exception.ConflictException;
import com.plazza.ridehailing.exception.DriverUnavailableException;
import com.plazza.ridehailing.exception.ResourceNotFoundException;
import com.plazza.ridehailing.model.*;
import com.plazza.ridehailing.repository.DriverRepository;
import com.plazza.ridehailing.repository.RideRepository;
import com.plazza.ridehailing.repository.UserRepository;
import com.plazza.ridehailing.service.matching.DriverMatchingStrategy;
import com.plazza.ridehailing.service.matching.DriverMatchingStrategyRegistry;
import com.plazza.ridehailing.service.pricing.PricingEngine;
import com.plazza.ridehailing.service.pricing.SurgePricingPolicy;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@Service
public class RideService {

    public static final double DEFAULT_RADIUS_KM = 5.0;
    public static final double CANCELLATION_FEE = 20.0;

    private final RideRepository rideRepository;
    private final UserRepository userRepository;
    private final DriverRepository driverRepository;
    private final CouponService couponService;
    private final PricingEngine pricingEngine;
    private final DriverMatchingStrategyRegistry matchingStrategyRegistry;
    private final SurgePricingPolicy surgePricingPolicy;

    public RideService(RideRepository rideRepository,
                       UserRepository userRepository,
                       DriverRepository driverRepository,
                       CouponService couponService,
                       PricingEngine pricingEngine,
                       DriverMatchingStrategyRegistry matchingStrategyRegistry,
                       SurgePricingPolicy surgePricingPolicy) {
        this.rideRepository = rideRepository;
        this.userRepository = userRepository;
        this.driverRepository = driverRepository;
        this.couponService = couponService;
        this.pricingEngine = pricingEngine;
        this.matchingStrategyRegistry = matchingStrategyRegistry;
        this.surgePricingPolicy = surgePricingPolicy;
    }

    public RideResponse bookRide(BookRideRequest request) {
        // 1. Validate user
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + request.getUserId()));

        if (rideRepository.findActiveRideByUserId(user.getId()).isPresent()) {
            throw new ConflictException("User already has an ongoing or booked ride");
        }

        Location pickup = request.getPickupLocation();
        Location dropoff = request.getDropoffLocation();
        if (pickup == null || dropoff == null) {
            throw new BadRequestException("Pickup and dropoff locations are mandatory");
        }

        double radius = (request.getRadiusKm() != null && request.getRadiusKm() > 0)
                ? request.getRadiusKm() : DEFAULT_RADIUS_KM;

        // 2. Validate coupon if provided
        Coupon coupon = null;
        if (request.getCouponCode() != null && !request.getCouponCode().isBlank()) {
            coupon = couponService.getValidCoupon(request.getCouponCode());
        }

        // 3. Driver Matching with strategy & Concurrency-safe assignment
        DriverMatchingStrategy strategy = matchingStrategyRegistry.getStrategy(request.getMatchingStrategy());

        // Step 3a: Attempt to find and assign requested car type driver within radius
        List<Driver> candidates = driverRepository.findAvailableDriversByCarType(request.getCarType()).stream()
                .filter(d -> d.getCurrentLocation() != null && d.getCurrentLocation().distanceTo(pickup) <= radius)
                .toList();

        List<Driver> rankedCandidates = strategy.rankDrivers(candidates, pickup);
        Driver assignedDriver = null;
        boolean isUpgraded = false;

        for (Driver candidate : rankedCandidates) {
            if (driverRepository.tryAssignDriver(candidate.getId())) {
                assignedDriver = candidate;
                break;
            }
        }

        // Step 3b: Free Upgrade Rule - If Hatchback requested but unavailable, upgrade to Sedan for free!
        if (assignedDriver == null && request.getCarType() == CarType.HATCHBACK) {
            List<Driver> sedanCandidates = driverRepository.findAvailableDriversByCarType(CarType.SEDAN).stream()
                    .filter(d -> d.getCurrentLocation() != null && d.getCurrentLocation().distanceTo(pickup) <= radius)
                    .toList();

            List<Driver> rankedSedans = strategy.rankDrivers(sedanCandidates, pickup);
            for (Driver sedanDriver : rankedSedans) {
                if (driverRepository.tryAssignDriver(sedanDriver.getId())) {
                    assignedDriver = sedanDriver;
                    isUpgraded = true;
                    break;
                }
            }
        }

        if (assignedDriver == null) {
            throw new DriverUnavailableException(
                    "No drivers available within " + radius + " km for " + request.getCarType()
                            + (request.getCarType() == CarType.HATCHBACK ? " (including free Sedan upgrade)" : "")
            );
        }

        // 4. Calculate initial estimated fare breakdown
        double distanceKm = pickup.distanceTo(dropoff);
        FareBreakdown fareBreakdown = pricingEngine.calculateFare(
                distanceKm,
                request.getCarType(),
                assignedDriver.getCarType(),
                coupon
        );

        // 5. Construct and save Ride
        Ride ride = new Ride(
                null,
                user.getId(),
                assignedDriver.getId(),
                request.getCarType(),
                assignedDriver.getCarType(),
                isUpgraded,
                pickup,
                dropoff,
                coupon != null ? coupon.getCode() : null
        );
        ride.setFareBreakdown(fareBreakdown);
        ride.setStatus(RideStatus.ONGOING);

        Ride savedRide = rideRepository.save(ride);

        // Update user location to pickup
        user.setCurrentLocation(pickup);
        userRepository.save(user);

        return RideResponse.from(savedRide, assignedDriver);
    }

    public RideResponse endRide(String rideId) {
        Ride ride = rideRepository.findById(rideId)
                .orElseThrow(() -> new ResourceNotFoundException("Ride not found with id: " + rideId));

        if (ride.getStatus() != RideStatus.BOOKED && ride.getStatus() != RideStatus.ONGOING) {
            throw new BadRequestException("Ride cannot be ended because it is already " + ride.getStatus());
        }

        // Calculate final fare
        double distanceKm = ride.getPickupLocation().distanceTo(ride.getDropoffLocation());
        Coupon coupon = null;
        if (ride.getAppliedCouponCode() != null) {
            coupon = couponService.getValidCoupon(ride.getAppliedCouponCode());
        }

        FareBreakdown finalFare = pricingEngine.calculateFare(
                distanceKm,
                ride.getRequestedCarType(),
                ride.getAssignedCarType(),
                coupon
        );

        ride.setDistanceKm(distanceKm);
        ride.setFareBreakdown(finalFare);
        ride.setStatus(RideStatus.COMPLETED);
        ride.setCompletedAt(Instant.now());

        // Update driver location to dropoff and release status to AVAILABLE
        Driver driver = driverRepository.findById(ride.getDriverId()).orElse(null);
        if (driver != null) {
            driver.setCurrentLocation(ride.getDropoffLocation());
            driver.setTotalRidesCompleted(driver.getTotalRidesCompleted() + 1);
            driverRepository.save(driver);
            driverRepository.releaseDriver(driver.getId());
        }

        // Update user location to dropoff
        userRepository.findById(ride.getUserId()).ifPresent(user -> {
            user.setCurrentLocation(ride.getDropoffLocation());
            userRepository.save(user);
        });

        Ride saved = rideRepository.save(ride);
        return RideResponse.from(saved, driver);
    }

    public RideResponse cancelRide(String rideId) {
        Ride ride = rideRepository.findById(rideId)
                .orElseThrow(() -> new ResourceNotFoundException("Ride not found with id: " + rideId));

        if (ride.getStatus() == RideStatus.COMPLETED) {
            throw new BadRequestException("Completed ride cannot be cancelled");
        }
        if (ride.getStatus() == RideStatus.CANCELLED) {
            throw new BadRequestException("Ride is already cancelled");
        }

        // Apply cancellation policy
        FareBreakdown breakdown = ride.getFareBreakdown();
        if (breakdown == null) {
            breakdown = new FareBreakdown();
        }
        breakdown.setCancellationFee(CANCELLATION_FEE);
        breakdown.setFinalFare(CANCELLATION_FEE);

        ride.setFareBreakdown(breakdown);
        ride.setStatus(RideStatus.CANCELLED);
        ride.setCancelledAt(Instant.now());

        // Release driver
        driverRepository.releaseDriver(ride.getDriverId());
        Driver driver = driverRepository.findById(ride.getDriverId()).orElse(null);

        Ride saved = rideRepository.save(ride);
        return RideResponse.from(saved, driver);
    }

    public List<RideResponse> getUserRides(String userId, String statusFilter) {
        if (!userRepository.existsById(userId)) {
            throw new ResourceNotFoundException("User not found with id: " + userId);
        }

        List<Ride> rides;
        if ("ONGOING".equalsIgnoreCase(statusFilter)) {
            rides = rideRepository.findByUserIdAndStatusIn(userId, List.of(RideStatus.BOOKED, RideStatus.ONGOING));
        } else if ("COMPLETED".equalsIgnoreCase(statusFilter)) {
            rides = rideRepository.findByUserIdAndStatusIn(userId, List.of(RideStatus.COMPLETED));
        } else {
            rides = rideRepository.findByUserId(userId);
        }

        return rides.stream()
                .map(r -> RideResponse.from(r, driverRepository.findById(r.getDriverId()).orElse(null)))
                .toList();
    }

    public List<RideResponse> getDriverRides(String driverId, String statusFilter) {
        Driver driver = driverRepository.findById(driverId)
                .orElseThrow(() -> new ResourceNotFoundException("Driver not found with id: " + driverId));

        List<Ride> rides;
        if ("ONGOING".equalsIgnoreCase(statusFilter)) {
            rides = rideRepository.findByDriverIdAndStatusIn(driverId, List.of(RideStatus.BOOKED, RideStatus.ONGOING));
        } else if ("COMPLETED".equalsIgnoreCase(statusFilter)) {
            rides = rideRepository.findByDriverIdAndStatusIn(driverId, List.of(RideStatus.COMPLETED));
        } else {
            rides = rideRepository.findByDriverId(driverId);
        }

        return rides.stream()
                .map(r -> RideResponse.from(r, driver))
                .toList();
    }

    public void setSurgeMultiplier(double multiplier) {
        surgePricingPolicy.setSurgeMultiplier(multiplier);
    }
}
