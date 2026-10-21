package com.plazza.ridehailing.service;

import com.plazza.ridehailing.dto.request.BookRideRequest;
import com.plazza.ridehailing.dto.response.RideResponse;
import com.plazza.ridehailing.exception.ConflictException;
import com.plazza.ridehailing.exception.DriverUnavailableException;
import com.plazza.ridehailing.model.*;
import com.plazza.ridehailing.repository.*;
import com.plazza.ridehailing.service.matching.*;
import com.plazza.ridehailing.service.pricing.DefaultSurgePricingPolicy;
import com.plazza.ridehailing.service.pricing.TieredPricingEngine;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Ride Service & Booking Integration Tests")
class RideServiceTest {

    private UserRepository userRepository;
    private DriverRepository driverRepository;
    private CouponRepository couponRepository;
    private RideRepository rideRepository;
    private CouponService couponService;
    private TieredPricingEngine pricingEngine;
    private DriverMatchingStrategyRegistry matchingRegistry;
    private DefaultSurgePricingPolicy surgePolicy;
    private RideService rideService;

    @BeforeEach
    void setUp() {
        userRepository = new InMemoryUserRepository();
        driverRepository = new InMemoryDriverRepository();
        couponRepository = new InMemoryCouponRepository();
        rideRepository = new InMemoryRideRepository();

        couponService = new CouponService(couponRepository);
        surgePolicy = new DefaultSurgePricingPolicy();
        pricingEngine = new TieredPricingEngine(surgePolicy);

        NearestDriverMatchingStrategy nearestStrategy = new NearestDriverMatchingStrategy();
        HighestRatedDriverMatchingStrategy highestRatedStrategy = new HighestRatedDriverMatchingStrategy();
        matchingRegistry = new DriverMatchingStrategyRegistry(List.of(nearestStrategy, highestRatedStrategy));

        rideService = new RideService(
                rideRepository,
                userRepository,
                driverRepository,
                couponService,
                pricingEngine,
                matchingRegistry,
                surgePolicy
        );
    }

    @Test
    @DisplayName("Should successfully book a ride with nearest driver within radius")
    void testSuccessfulRideBooking() {
        User user = userRepository.save(new User(null, "Rahul", "rahul@test.com", "9876543210", new Location(0.0, 0.0)));

        // Driver 1: 2 km away
        Driver d1 = driverRepository.save(new Driver(null, "Driver 1", "9000000001", CarType.HATCHBACK, "KA01AB1234", new Location(0.0, 2.0), 4.5));
        // Driver 2: 4 km away
        driverRepository.save(new Driver(null, "Driver 2", "9000000002", CarType.HATCHBACK, "KA01AB5678", new Location(0.0, 4.0), 4.8));

        BookRideRequest req = new BookRideRequest(
                user.getId(),
                new Location(0.0, 0.0),
                new Location(0.0, 10.0),
                CarType.HATCHBACK,
                null,
                5.0,
                MatchingStrategyType.NEAREST
        );

        RideResponse response = rideService.bookRide(req);

        assertNotNull(response.getId());
        assertEquals(d1.getId(), response.getDriverId(), "Should match Driver 1 (closer at 2km)");
        assertEquals(RideStatus.ONGOING, response.getStatus());
        assertEquals(CarType.HATCHBACK, response.getAssignedCarType());
        assertFalse(response.isUpgraded());

        // Driver should now be on ride
        Driver assigned = driverRepository.findById(d1.getId()).orElseThrow();
        assertEquals(DriverStatus.ON_RIDE, assigned.getStatus());
    }

    @Test
    @DisplayName("Should upgrade to Sedan at no extra cost when Hatchback is unavailable")
    void testFreeUpgradeToSedanWhenHatchbackUnavailable() {
        User user = userRepository.save(new User(null, "Priya", "priya@test.com", "9876543211", new Location(0.0, 0.0)));

        // Only a SEDAN driver is available nearby
        Driver sedanDriver = driverRepository.save(new Driver(null, "Sedan King", "9000000003", CarType.SEDAN, "KA01CD9999", new Location(0.0, 1.5), 4.9));

        BookRideRequest req = new BookRideRequest(
                user.getId(),
                new Location(0.0, 0.0),
                new Location(0.0, 10.0),
                CarType.HATCHBACK, // User asked for Hatchback
                null,
                5.0,
                MatchingStrategyType.NEAREST
        );

        RideResponse response = rideService.bookRide(req);

        assertEquals(sedanDriver.getId(), response.getDriverId());
        assertEquals(CarType.HATCHBACK, response.getRequestedCarType());
        assertEquals(CarType.SEDAN, response.getAssignedCarType());
        assertTrue(response.isUpgraded(), "Should be flagged as upgraded free");

        // Distance 10 km: Hatchback rate = ₹69.0, Sedan regular rate = ₹103.5
        // Upgraded ride must be billed at Hatchback price ₹69.0!
        assertEquals(69.0, response.getFinalFare());
    }

    @Test
    @DisplayName("Should match highest-rated driver when HIGHEST_RATED matching strategy is requested")
    void testHighestRatedMatchingStrategy() {
        User user = userRepository.save(new User(null, "Amit", "amit@test.com", "9876543212", new Location(0.0, 0.0)));

        // Driver 1: Closer (1 km), but lower rating (4.2)
        driverRepository.save(new Driver(null, "Driver Close", "9000000010", CarType.HATCHBACK, "KA01A1", new Location(0.0, 1.0), 4.2));
        // Driver 2: Farther (3 km), but higher rating (4.9)
        Driver topDriver = driverRepository.save(new Driver(null, "Driver Top", "9000000011", CarType.HATCHBACK, "KA01A2", new Location(0.0, 3.0), 4.9));

        BookRideRequest req = new BookRideRequest(
                user.getId(),
                new Location(0.0, 0.0),
                new Location(0.0, 5.0),
                CarType.HATCHBACK,
                null,
                5.0,
                MatchingStrategyType.HIGHEST_RATED
        );

        RideResponse response = rideService.bookRide(req);

        assertEquals(topDriver.getId(), response.getDriverId(), "Should pick higher-rated driver");
    }

    @Test
    @DisplayName("Should throw DriverUnavailableException if no drivers within radius")
    void testNoDriverInRadiusThrowsException() {
        User user = userRepository.save(new User(null, "Vikram", "vikram@test.com", "9876543213", new Location(0.0, 0.0)));

        // Driver 10 km away, user searches within 3 km
        driverRepository.save(new Driver(null, "Far Driver", "9000000020", CarType.HATCHBACK, "KA01FAR", new Location(0.0, 10.0), 4.5));

        BookRideRequest req = new BookRideRequest(
                user.getId(),
                new Location(0.0, 0.0),
                new Location(0.0, 5.0),
                CarType.HATCHBACK,
                null,
                3.0,
                MatchingStrategyType.NEAREST
        );

        assertThrows(DriverUnavailableException.class, () -> rideService.bookRide(req));
    }

    @Test
    @DisplayName("Should complete ride, calculate final fare, and release driver at dropoff location")
    void testEndRideLifecycle() {
        User user = userRepository.save(new User(null, "Neha", "neha@test.com", "9876543214", new Location(0.0, 0.0)));
        Driver driver = driverRepository.save(new Driver(null, "Driver D", "9000000030", CarType.HATCHBACK, "KA01D", new Location(0.0, 1.0), 4.5));

        BookRideRequest req = new BookRideRequest(
                user.getId(),
                new Location(0.0, 0.0),
                new Location(0.0, 4.0), // 4 km ride -> 2 * 10 + 2 * 8 = 36 -> min fare ₹50 applies
                CarType.HATCHBACK,
                null,
                5.0,
                MatchingStrategyType.NEAREST
        );

        RideResponse booking = rideService.bookRide(req);
        RideResponse endedRide = rideService.endRide(booking.getId());

        assertEquals(RideStatus.COMPLETED, endedRide.getStatus());
        assertEquals(50.0, endedRide.getFinalFare(), "Minimum fare ₹50 should apply");
        assertNotNull(endedRide.getCompletedAt());

        // Driver check: status available, location updated to dropoff (0.0, 4.0), ride count incremented
        Driver updatedDriver = driverRepository.findById(driver.getId()).orElseThrow();
        assertEquals(DriverStatus.AVAILABLE, updatedDriver.getStatus());
        assertEquals(new Location(0.0, 4.0), updatedDriver.getCurrentLocation());
        assertEquals(1, updatedDriver.getTotalRidesCompleted());
    }

    @Test
    @DisplayName("Should cancel ride, apply cancellation fee, and release driver")
    void testCancelRide() {
        User user = userRepository.save(new User(null, "Karan", "karan@test.com", "9876543215", new Location(0.0, 0.0)));
        Driver driver = driverRepository.save(new Driver(null, "Driver C", "9000000040", CarType.HATCHBACK, "KA01C", new Location(0.0, 1.0), 4.5));

        BookRideRequest req = new BookRideRequest(
                user.getId(),
                new Location(0.0, 0.0),
                new Location(0.0, 4.0),
                CarType.HATCHBACK,
                null,
                5.0,
                MatchingStrategyType.NEAREST
        );

        RideResponse booking = rideService.bookRide(req);
        RideResponse cancelled = rideService.cancelRide(booking.getId());

        assertEquals(RideStatus.CANCELLED, cancelled.getStatus());
        assertEquals(20.0, cancelled.getFinalFare(), "Cancellation fee ₹20 should apply");

        Driver updatedDriver = driverRepository.findById(driver.getId()).orElseThrow();
        assertEquals(DriverStatus.AVAILABLE, updatedDriver.getStatus(), "Driver must be released back to AVAILABLE");
    }

    @Test
    @DisplayName("Concurrency Bonus: Handle simultaneous booking race conditions safely")
    void testConcurrentBookingSafety() throws InterruptedException {
        // Only ONE driver available
        Driver driver = driverRepository.save(new Driver(null, "Single Driver", "9000000099", CarType.HATCHBACK, "KA01RACE", new Location(0.0, 1.0), 4.5));

        User user1 = userRepository.save(new User(null, "User 1", "u1@test.com", "9876543001", new Location(0.0, 0.0)));
        User user2 = userRepository.save(new User(null, "User 2", "u2@test.com", "9876543002", new Location(0.0, 0.0)));

        int threads = 2;
        ExecutorService executor = Executors.newFixedThreadPool(threads);
        CountDownLatch startLatch = new CountDownLatch(1);
        CountDownLatch finishLatch = new CountDownLatch(threads);

        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger failureCount = new AtomicInteger(0);

        executor.submit(() -> {
            try {
                startLatch.await();
                rideService.bookRide(new BookRideRequest(user1.getId(), new Location(0.0, 0.0), new Location(0.0, 5.0), CarType.HATCHBACK, null, 5.0, MatchingStrategyType.NEAREST));
                successCount.incrementAndGet();
            } catch (Exception e) {
                failureCount.incrementAndGet();
            } finally {
                finishLatch.countDown();
            }
        });

        executor.submit(() -> {
            try {
                startLatch.await();
                rideService.bookRide(new BookRideRequest(user2.getId(), new Location(0.0, 0.0), new Location(0.0, 5.0), CarType.HATCHBACK, null, 5.0, MatchingStrategyType.NEAREST));
                successCount.incrementAndGet();
            } catch (Exception e) {
                failureCount.incrementAndGet();
            } finally {
                finishLatch.countDown();
            }
        });

        startLatch.countDown(); // Fire both threads at the exact same instant!
        finishLatch.await();
        executor.shutdown();

        // Exactly one should succeed, and exactly one should fail with DriverUnavailableException!
        assertEquals(1, successCount.get(), "Only 1 user must successfully book the single driver");
        assertEquals(1, failureCount.get(), "The other user must fail cleanly without double-booking");

        Driver d = driverRepository.findById(driver.getId()).orElseThrow();
        assertEquals(DriverStatus.ON_RIDE, d.getStatus());
    }
}
