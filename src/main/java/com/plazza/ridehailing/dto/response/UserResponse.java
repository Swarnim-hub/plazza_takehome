package com.plazza.ridehailing.dto.response;

import com.plazza.ridehailing.model.Location;
import com.plazza.ridehailing.model.User;

import java.time.Instant;

public class UserResponse {
    private String id;
    private String name;
    private String email;
    private String phone;
    private Location currentLocation;
    private Instant createdAt;

    public UserResponse() {
    }

    public static UserResponse from(User user) {
        if (user == null) return null;
        UserResponse response = new UserResponse();
        response.setId(user.getId());
        response.setName(user.getName());
        response.setEmail(user.getEmail());
        response.setPhone(user.getPhone());
        response.setCurrentLocation(user.getCurrentLocation());
        response.setCreatedAt(user.getCreatedAt());
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public Location getCurrentLocation() {
        return currentLocation;
    }

    public void setCurrentLocation(Location currentLocation) {
        this.currentLocation = currentLocation;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }
}
