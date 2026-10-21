package com.plazza.ridehailing.controller;

import com.plazza.ridehailing.dto.request.RegisterUserRequest;
import com.plazza.ridehailing.dto.request.UpdateLocationRequest;
import com.plazza.ridehailing.dto.response.UserResponse;
import com.plazza.ridehailing.model.Location;
import com.plazza.ridehailing.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/users")
@Tag(name = "User Management", description = "Endpoints for user registration, profile lookup, and location updates")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    @Operation(summary = "Register a new user", description = "Registers a rider user with name, phone, optional email and location")
    public ResponseEntity<UserResponse> registerUser(@Valid @RequestBody RegisterUserRequest request) {
        UserResponse response = userService.registerUser(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get user by ID", description = "Fetches a user profile by unique user ID")
    public ResponseEntity<UserResponse> getUserById(@PathVariable String id) {
        return ResponseEntity.ok(userService.getUserById(id));
    }

    @GetMapping
    @Operation(summary = "List all users", description = "Retrieves all registered users in the platform")
    public ResponseEntity<List<UserResponse>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    @PutMapping("/{id}/location")
    @Operation(summary = "Update user current location", description = "Updates coordinates for an existing user")
    public ResponseEntity<UserResponse> updateLocation(@PathVariable String id,
                                                       @Valid @RequestBody UpdateLocationRequest request) {
        Location location = new Location(request.getLatitude(), request.getLongitude());
        return ResponseEntity.ok(userService.updateUserLocation(id, location));
    }
}
