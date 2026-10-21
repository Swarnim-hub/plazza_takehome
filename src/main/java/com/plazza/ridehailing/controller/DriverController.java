package com.plazza.ridehailing.controller;

import com.plazza.ridehailing.dto.request.RegisterDriverRequest;
import com.plazza.ridehailing.dto.request.UpdateLocationRequest;
import com.plazza.ridehailing.dto.response.DriverResponse;
import com.plazza.ridehailing.model.CarType;
import com.plazza.ridehailing.model.DriverStatus;
import com.plazza.ridehailing.service.DriverService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/drivers")
@Tag(name = "Driver & Cab Management", description = "Endpoints for driver registration, cab location tracking, and status updates")
public class DriverController {

    private final DriverService driverService;

    public DriverController(DriverService driverService) {
        this.driverService = driverService;
    }

    @PostMapping
    @Operation(summary = "Register a new driver", description = "Registers a driver with car type (HATCHBACK/SEDAN), vehicle number, and initial location")
    public ResponseEntity<DriverResponse> registerDriver(@Valid @RequestBody RegisterDriverRequest request) {
        DriverResponse response = driverService.registerDriver(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get driver by ID", description = "Fetches a driver profile and current status")
    public ResponseEntity<DriverResponse> getDriverById(@PathVariable String id) {
        return ResponseEntity.ok(driverService.getDriverById(id));
    }

    @GetMapping
    @Operation(summary = "List drivers", description = "Lists drivers with optional filter for available drivers and car type")
    public ResponseEntity<List<DriverResponse>> getDrivers(
            @RequestParam(required = false, defaultValue = "false") boolean availableOnly,
            @RequestParam(required = false) CarType carType) {
        if (availableOnly) {
            return ResponseEntity.ok(driverService.getAvailableDrivers(carType));
        }
        return ResponseEntity.ok(driverService.getAllDrivers());
    }

    @PutMapping("/{id}/location")
    @Operation(summary = "Update location of a cab/driver", description = "Updates coordinates of a driver's cab")
    public ResponseEntity<DriverResponse> updateLocation(@PathVariable String id,
                                                         @Valid @RequestBody UpdateLocationRequest request) {
        return ResponseEntity.ok(driverService.updateDriverLocation(id, request));
    }

    @PutMapping("/{id}/status")
    @Operation(summary = "Update driver availability status", description = "Updates driver status (AVAILABLE, ON_RIDE, OFFLINE)")
    public ResponseEntity<DriverResponse> updateStatus(@PathVariable String id,
                                                       @RequestParam DriverStatus status) {
        return ResponseEntity.ok(driverService.updateDriverStatus(id, status));
    }
}
