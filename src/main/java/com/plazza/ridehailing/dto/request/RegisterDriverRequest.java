package com.plazza.ridehailing.dto.request;

import com.plazza.ridehailing.model.CarType;
import com.plazza.ridehailing.model.Location;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

public class RegisterDriverRequest {
    @NotBlank(message = "Driver name cannot be blank")
    private String name;

    @NotBlank(message = "Phone number is required")
    @Pattern(regexp = "^[0-9]{10}$", message = "Phone must be a valid 10-digit number")
    private String phone;

    @NotNull(message = "Car type is required (HATCHBACK or SEDAN)")
    private CarType carType;

    @NotBlank(message = "Vehicle number is required")
    private String vehicleNumber;

    @NotNull(message = "Current location is required")
    private Location location;

    private Double rating;

    public RegisterDriverRequest() {
    }

    public RegisterDriverRequest(String name, String phone, CarType carType, String vehicleNumber,
                                  Location location, Double rating) {
        this.name = name;
        this.phone = phone;
        this.carType = carType;
        this.vehicleNumber = vehicleNumber;
        this.location = location;
        this.rating = rating;
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

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public Double getRating() {
        return rating;
    }

    public void setRating(Double rating) {
        this.rating = rating;
    }
}
