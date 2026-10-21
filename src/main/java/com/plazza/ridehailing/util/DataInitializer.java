package com.plazza.ridehailing.util;

import com.plazza.ridehailing.model.*;
import com.plazza.ridehailing.repository.CouponRepository;
import com.plazza.ridehailing.repository.DriverRepository;
import com.plazza.ridehailing.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DataInitializer.class);

    private final UserRepository userRepository;
    private final DriverRepository driverRepository;
    private final CouponRepository couponRepository;

    public DataInitializer(UserRepository userRepository,
                           DriverRepository driverRepository,
                           CouponRepository couponRepository) {
        this.userRepository = userRepository;
        this.driverRepository = driverRepository;
        this.couponRepository = couponRepository;
    }

    @Override
    public void run(String... args) {
        log.info("--- Pre-seeding Sample Demo Data for Live Testing ---");

        // Seed Users
        User u1 = new User("USR-101", "Aarav Sharma", "aarav@test.com", "9876543210", new Location(0.0, 0.0));
        User u2 = new User("USR-102", "Pooja Patel", "pooja@test.com", "9876543211", new Location(2.0, 2.0));
        User u3 = new User("USR-103", "Rohan Verma", "rohan@test.com", "9876543212", new Location(5.0, 5.0));
        userRepository.save(u1);
        userRepository.save(u2);
        userRepository.save(u3);

        // Seed Drivers
        // Driver 1: Hatchback, close to (0, 0), rating 4.6
        Driver d1 = new Driver("DRV-201", "Suresh Kumar", "9123456780", CarType.HATCHBACK, "KA-01-HB-1001", new Location(0.0, 1.0), 4.6);
        // Driver 2: Hatchback, 3 km from (0, 0), rating 4.9 (higher rating!)
        Driver d2 = new Driver("DRV-202", "Manish Rao", "9123456781", CarType.HATCHBACK, "KA-01-HB-1002", new Location(0.0, 3.0), 4.9);
        // Driver 3: Sedan, 1.5 km from (0, 0), rating 4.95
        Driver d3 = new Driver("DRV-203", "Rajesh Reddy", "9123456782", CarType.SEDAN, "KA-01-SD-2001", new Location(0.0, 1.5), 4.95);
        // Driver 4: Sedan, far away (15 km), rating 4.7
        Driver d4 = new Driver("DRV-204", "Vikram Singh", "9123456783", CarType.SEDAN, "KA-01-SD-2002", new Location(0.0, 15.0), 4.7);

        driverRepository.save(d1);
        driverRepository.save(d2);
        driverRepository.save(d3);
        driverRepository.save(d4);

        // Seed Coupons
        Coupon c1 = new Coupon("WELCOME50", DiscountType.PERCENTAGE, 50.0, 50.0, 100.0);
        Coupon c2 = new Coupon("FLAT30", DiscountType.FLAT_AMOUNT, 30.0, 50.0, 50.0);
        Coupon c3 = new Coupon("SAVE20", DiscountType.PERCENTAGE, 20.0, 50.0, 50.0);

        couponRepository.save(c1);
        couponRepository.save(c2);
        couponRepository.save(c3);

        log.info("Demo Data Initialized successfully:");
        log.info("Users: USR-101 (at 0,0), USR-102 (at 2,2), USR-103 (at 5,5)");
        log.info("Drivers: DRV-201 (Hatchback), DRV-202 (Hatchback high-rated), DRV-203 (Sedan), DRV-204 (Far Sedan)");
        log.info("Coupons: WELCOME50, FLAT30, SAVE20");
        log.info("Swagger UI available at: http://localhost:8080/swagger-ui.html");
    }
}
