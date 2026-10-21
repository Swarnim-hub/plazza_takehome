package com.plazza.ridehailing.dto.request;

import com.plazza.ridehailing.model.CarType;
import com.plazza.ridehailing.model.Location;
import com.plazza.ridehailing.service.matching.MatchingStrategyType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class BookRideRequest {
    @NotBlank(message = "User ID is required")
    private String userId;

    @NotNull(message = "Pickup location is required")
    private Location pickupLocation;

    @NotNull(message = "Dropoff location is required")
    private Location dropoffLocation;

    @NotNull(message = "Requested car type is required (HATCHBACK or SEDAN)")
    private CarType carType;

    private String couponCode;

    private Double radiusKm = 5.0;

    private MatchingStrategyType matchingStrategy = MatchingStrategyType.NEAREST;

    public BookRideRequest() {
    }

    public BookRideRequest(String userId, Location pickupLocation, Location dropoffLocation,
                           CarType carType, String couponCode, Double radiusKm,
                           MatchingStrategyType matchingStrategy) {
        this.userId = userId;
        this.pickupLocation = pickupLocation;
        this.dropoffLocation = dropoffLocation;
        this.carType = carType;
        this.couponCode = couponCode;
        this.radiusKm = radiusKm != null ? radiusKm : 5.0;
        this.matchingStrategy = matchingStrategy != null ? matchingStrategy : MatchingStrategyType.NEAREST;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public Location getPickupLocation() {
        return pickupLocation;
    }

    public void setPickupLocation(Location pickupLocation) {
        this.pickupLocation = pickupLocation;
    }

    public Location getDropoffLocation() {
        return dropoffLocation;
    }

    public void setDropoffLocation(Location dropoffLocation) {
        this.dropoffLocation = dropoffLocation;
    }

    public CarType getCarType() {
        return carType;
    }

    public void setCarType(CarType carType) {
        this.carType = carType;
    }

    public String getCouponCode() {
        return couponCode;
    }

    public void setCouponCode(String couponCode) {
        this.couponCode = couponCode;
    }

    public Double getRadiusKm() {
        return radiusKm;
    }

    public void setRadiusKm(Double radiusKm) {
        this.radiusKm = radiusKm;
    }

    public MatchingStrategyType getMatchingStrategy() {
        return matchingStrategy;
    }

    public void setMatchingStrategy(MatchingStrategyType matchingStrategy) {
        this.matchingStrategy = matchingStrategy;
    }
}
