package com.plazza.ridehailing.model;

import java.time.Instant;

public class User {
    private String id;
    private String name;
    private String email;
    private String phone;
    private Location currentLocation;
    private Instant createdAt;

    public User() {
        this.createdAt = Instant.now();
    }

    public User(String id, String name, String email, String phone, Location currentLocation) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.currentLocation = currentLocation;
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
