package com.plazza.ridehailing.controller;

import com.plazza.ridehailing.dto.request.BookRideRequest;
import com.plazza.ridehailing.dto.request.SetSurgeRequest;
import com.plazza.ridehailing.dto.response.RideResponse;
import com.plazza.ridehailing.service.RideService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/rides")
@Tag(name = "Ride Booking & Lifecycle", description = "Endpoints for booking rides, ending rides, cancellation, surge, and ride history")
public class RideController {

    private final RideService rideService;

    public RideController(RideService rideService) {
        this.rideService = rideService;
    }

    @PostMapping("/book")
    @Operation(summary = "Book a ride", description = "Books a ride matching available drivers within radius. Features automatic free upgrade to Sedan if Hatchback is unavailable.")
    public ResponseEntity<RideResponse> bookRide(@Valid @RequestBody BookRideRequest request) {
        RideResponse response = rideService.bookRide(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PostMapping("/{id}/end")
    @Operation(summary = "End a ride", description = "Completes the ride, calculates the tiered fare, applies coupons, updates cab location to dropoff, and releases driver.")
    public ResponseEntity<RideResponse> endRide(@PathVariable String id) {
        return ResponseEntity.ok(rideService.endRide(id));
    }

    @PostMapping("/{id}/cancel")
    @Operation(summary = "Cancel a ride", description = "Cancels a booked ride and applies standard cancellation fee policy.")
    public ResponseEntity<RideResponse> cancelRide(@PathVariable String id) {
        return ResponseEntity.ok(rideService.cancelRide(id));
    }

    @GetMapping("/user/{userId}")
    @Operation(summary = "User ride history", description = "Retrieves user's ride history. Filter by status=ONGOING or status=COMPLETED.")
    public ResponseEntity<List<RideResponse>> getUserRides(
            @PathVariable String userId,
            @RequestParam(required = false) String status) {
        return ResponseEntity.ok(rideService.getUserRides(userId, status));
    }

    @GetMapping("/driver/{driverId}")
    @Operation(summary = "Driver ride history", description = "Retrieves driver's ride history. Filter by status=ONGOING or status=COMPLETED.")
    public ResponseEntity<List<RideResponse>> getDriverRides(
            @PathVariable String driverId,
            @RequestParam(required = false) String status) {
        return ResponseEntity.ok(rideService.getDriverRides(driverId, status));
    }

    @PostMapping("/surge")
    @Operation(summary = "Set dynamic surge multiplier", description = "Pluggable multiplier for surge pricing based on market demand/supply.")
    public ResponseEntity<String> setSurgeMultiplier(@Valid @RequestBody SetSurgeRequest request) {
        rideService.setSurgeMultiplier(request.getMultiplier());
        return ResponseEntity.ok("Surge multiplier updated to: " + request.getMultiplier() + "x");
    }
}
