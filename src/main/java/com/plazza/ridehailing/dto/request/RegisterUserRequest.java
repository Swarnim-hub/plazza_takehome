package com.plazza.ridehailing.dto.request;

import com.plazza.ridehailing.model.Location;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public class RegisterUserRequest {
    @NotBlank(message = "Name cannot be blank")
    private String name;

    private String email;

    @NotBlank(message = "Phone number is required")
    @Pattern(regexp = "^[0-9]{10}$", message = "Phone must be a valid 10-digit number")
    private String phone;

    private Location location;

    public RegisterUserRequest() {
    }

    public RegisterUserRequest(String name, String email, String phone, Location location) {
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.location = location;
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

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }
}
