package com.plazza.ridehailing.dto.response;

import com.plazza.ridehailing.model.CarType;
import com.plazza.ridehailing.model.Driver;
import com.plazza.ridehailing.model.FareBreakdown;
import com.plazza.ridehailing.model.Location;
import com.plazza.ridehailing.model.Ride;
import com.plazza.ridehailing.model.RideStatus;

import java.time.Instant;

public class RideResponse {
    private String id;
    private String userId;
    private String driverId;
    private String driverName;
    private String driverPhone;
    private String vehicleNumber;
    private CarType requestedCarType;
    private CarType assignedCarType;
    private boolean upgraded;
    private Location pickupLocation;
    private Location dropoffLocation;
    private double distanceKm;
    private RideStatus status;
    private String couponCode;
    private FareBreakdown fareBreakdown;
    private Double finalFare;
    private Instant createdAt;
    private Instant startedAt;
    private Instant completedAt;
    private Instant cancelledAt;

    public RideResponse() {
    }

    public static RideResponse from(Ride ride, Driver driver) {
        if (ride == null) return null;
        RideResponse res = new RideResponse();
        res.setId(ride.getId());
        res.setUserId(ride.getUserId());
        res.setDriverId(ride.getDriverId());
        if (driver != null) {
            res.setDriverName(driver.getName());
            res.setDriverPhone(driver.getPhone());
            res.setVehicleNumber(driver.getVehicleNumber());
        }
        res.setRequestedCarType(ride.getRequestedCarType());
        res.setAssignedCarType(ride.getAssignedCarType());
        res.setUpgraded(ride.isUpgraded());
        res.setPickupLocation(ride.getPickupLocation());
        res.setDropoffLocation(ride.getDropoffLocation());
        res.setDistanceKm(ride.getDistanceKm());
        res.setStatus(ride.getStatus());
        res.setCouponCode(ride.getAppliedCouponCode());
        res.setFareBreakdown(ride.getFareBreakdown());
        if (ride.getFareBreakdown() != null) {
            res.setFinalFare(ride.getFareBreakdown().getFinalFare());
        }
        res.setCreatedAt(ride.getCreatedAt());
        res.setStartedAt(ride.getStartedAt());
        res.setCompletedAt(ride.getCompletedAt());
        res.setCancelledAt(ride.getCancelledAt());
        return res;
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

    public String getDriverName() {
        return driverName;
    }

    public void setDriverName(String driverName) {
        this.driverName = driverName;
    }

    public String getDriverPhone() {
        return driverPhone;
    }

    public void setDriverPhone(String driverPhone) {
        this.driverPhone = driverPhone;
    }

    public String getVehicleNumber() {
        return vehicleNumber;
    }

    public void setVehicleNumber(String vehicleNumber) {
        this.vehicleNumber = vehicleNumber;
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

    public String getCouponCode() {
        return couponCode;
    }

    public void setCouponCode(String couponCode) {
        this.couponCode = couponCode;
    }

    public FareBreakdown getFareBreakdown() {
        return fareBreakdown;
    }

    public void setFareBreakdown(FareBreakdown fareBreakdown) {
        this.fareBreakdown = fareBreakdown;
    }

    public Double getFinalFare() {
        return finalFare;
    }

    public void setFinalFare(Double finalFare) {
        this.finalFare = finalFare;
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
