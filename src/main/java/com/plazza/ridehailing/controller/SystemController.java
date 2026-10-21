package com.plazza.ridehailing.controller;

import com.plazza.ridehailing.repository.CouponRepository;
import com.plazza.ridehailing.repository.DriverRepository;
import com.plazza.ridehailing.repository.RideRepository;
import com.plazza.ridehailing.repository.UserRepository;
import com.plazza.ridehailing.util.DataInitializer;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/system")
@Tag(name = "System & Testing Utilities", description = "Endpoints to reset the test database and restore fresh demo state")
public class SystemController {

    private final UserRepository userRepository;
    private final DriverRepository driverRepository;
    private final RideRepository rideRepository;
    private final CouponRepository couponRepository;
    private final DataInitializer dataInitializer;

    public SystemController(UserRepository userRepository,
                            DriverRepository driverRepository,
                            RideRepository rideRepository,
                            CouponRepository couponRepository,
                            DataInitializer dataInitializer) {
        this.userRepository = userRepository;
        this.driverRepository = driverRepository;
        this.rideRepository = rideRepository;
        this.couponRepository = couponRepository;
        this.dataInitializer = dataInitializer;
    }

    @PostMapping("/reset")
    @Operation(summary = "Reset database to clean initial demo state",
            description = "Clears all manually created users, drivers, coupons, and rides, and restores only the fresh demo seed data.")
    public ResponseEntity<String> resetDatabase() {
        userRepository.clear();
        driverRepository.clear();
        rideRepository.clear();
        couponRepository.clear();
        dataInitializer.run();
        return ResponseEntity.ok("Database successfully reset to clean initial demo state!");
    }
}
