package com.plazza.ridehailing.dto.response;

import com.plazza.ridehailing.model.CarType;
import com.plazza.ridehailing.model.Driver;
import com.plazza.ridehailing.model.DriverStatus;
import com.plazza.ridehailing.model.Location;

import java.time.Instant;

public class DriverResponse {
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

    public DriverResponse() {
    }

    public static DriverResponse from(Driver driver) {
        if (driver == null) return null;
        DriverResponse response = new DriverResponse();
        response.setId(driver.getId());
        response.setName(driver.getName());
        response.setPhone(driver.getPhone());
        response.setCarType(driver.getCarType());
        response.setVehicleNumber(driver.getVehicleNumber());
        response.setCurrentLocation(driver.getCurrentLocation());
        response.setStatus(driver.getStatus());
        response.setRating(driver.getRating());
        response.setTotalRidesCompleted(driver.getTotalRidesCompleted());
        response.setCreatedAt(driver.getCreatedAt());
        return response;
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
}
