package com.plazza.ridehailing.model;

import java.time.Instant;

public class Ride {
    private String id;
    private String userId;
    private String driverId;
    private CarType requestedCarType;
    private CarType assignedCarType;
    private boolean upgraded;
    private Location pickupLocation;
    private Location dropoffLocation;
    private double distanceKm;
    private RideStatus status;
    private String appliedCouponCode;
    private FareBreakdown fareBreakdown;
    private Instant createdAt;
    private Instant startedAt;
    private Instant completedAt;
    private Instant cancelledAt;

    public Ride() {
        this.status = RideStatus.BOOKED;
        this.createdAt = Instant.now();
    }

    public Ride(String id, String userId, String driverId, CarType requestedCarType, CarType assignedCarType,
                boolean upgraded, Location pickupLocation, Location dropoffLocation, String appliedCouponCode) {
        this.id = id;
        this.userId = userId;
        this.driverId = driverId;
        this.requestedCarType = requestedCarType;
        this.assignedCarType = assignedCarType;
        this.upgraded = upgraded;
        this.pickupLocation = pickupLocation;
        this.dropoffLocation = dropoffLocation;
        this.appliedCouponCode = appliedCouponCode;
        this.status = RideStatus.BOOKED;
        this.createdAt = Instant.now();
        this.startedAt = Instant.now();
        if (pickupLocation != null && dropoffLocation != null) {
            this.distanceKm = pickupLocation.distanceTo(dropoffLocation);
        }
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getDriverId() {
        return driverId;
    }

    public void setDriverId(String driverId) {
        this.driverId = driverId;
    }

    public CarType getRequestedCarType() {
        return requestedCarType;
    }

    public void setRequestedCarType(CarType requestedCarType) {
        this.requestedCarType = requestedCarType;
    }

    public CarType getAssignedCarType() {
        return assignedCarType;
    }

    public void setAssignedCarType(CarType assignedCarType) {
        this.assignedCarType = assignedCarType;
    }

    public boolean isUpgraded() {
        return upgraded;
    }

    public void setUpgraded(boolean upgraded) {
        this.upgraded = upgraded;
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

    public double getDistanceKm() {
        return distanceKm;
    }

    public void setDistanceKm(double distanceKm) {
        this.distanceKm = distanceKm;
    }

    public RideStatus getStatus() {
        return status;
    }

    public void setStatus(RideStatus status) {
        this.status = status;
    }

    public String getAppliedCouponCode() {
        return appliedCouponCode;
    }

    public void setAppliedCouponCode(String appliedCouponCode) {
        this.appliedCouponCode = appliedCouponCode;
    }

    public FareBreakdown getFareBreakdown() {
        return fareBreakdown;
    }

    public void setFareBreakdown(FareBreakdown fareBreakdown) {
        this.fareBreakdown = fareBreakdown;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public Instant getStartedAt() {
        return startedAt;
    }

    public void setStartedAt(Instant startedAt) {
        this.startedAt = startedAt;
    }

    public Instant getCompletedAt() {
        return completedAt;
    }

    public void setCompletedAt(Instant completedAt) {
        this.completedAt = completedAt;
    }

    public Instant getCancelledAt() {
        return cancelledAt;
    }

    public void setCancelledAt(Instant cancelledAt) {
        this.cancelledAt = cancelledAt;
    }
}
