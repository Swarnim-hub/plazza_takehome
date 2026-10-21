package com.plazza.ridehailing.model;

import java.time.Instant;

public class Driver {
    private String id;
    private String name;
    private String phone;
    private CarType carType;
    private String vehicleNumber;
    private Location currentLocation;
    private DriverStatus status;
    private double rating;
    private int totalRidesCompleted;
    private Instant createdAt;

    public Driver() {
        this.status = DriverStatus.AVAILABLE;
        this.rating = 5.0;
        this.totalRidesCompleted = 0;
        this.createdAt = Instant.now();
    }

    public Driver(String id, String name, String phone, CarType carType, String vehicleNumber,
                  Location currentLocation, double rating) {
        this.id = id;
        this.name = name;
        this.phone = phone;
        this.carType = carType;
        this.vehicleNumber = vehicleNumber;
        this.currentLocation = currentLocation;
        this.status = DriverStatus.AVAILABLE;
        this.rating = rating;
        this.totalRidesCompleted = 0;
        this.createdAt = Instant.now();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public CarType getCarType() {
        return carType;
    }

    public void setCarType(CarType carType) {
        this.carType = carType;
    }

    public String getVehicleNumber() {
        return vehicleNumber;
    }

    public void setVehicleNumber(String vehicleNumber) {
        this.vehicleNumber = vehicleNumber;
    }

    public Location getCurrentLocation() {
        return currentLocation;
    }

    public void setCurrentLocation(Location currentLocation) {
        this.currentLocation = currentLocation;
    }

    public DriverStatus getStatus() {
        return status;
    }

    public void setStatus(DriverStatus status) {
        this.status = status;
    }

    public double getRating() {
        return rating;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }

    public int getTotalRidesCompleted() {
        return totalRidesCompleted;
    }

    public void setTotalRidesCompleted(int totalRidesCompleted) {
        this.totalRidesCompleted = totalRidesCompleted;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public boolean isAvailable() {
        return this.status == DriverStatus.AVAILABLE;
    }
}
