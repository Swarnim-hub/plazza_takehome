package com.plazza.ridehailing.repository;

import com.plazza.ridehailing.model.CarType;
import com.plazza.ridehailing.model.Driver;
import com.plazza.ridehailing.model.DriverStatus;
import com.plazza.ridehailing.model.Location;

import java.util.List;
import java.util.Optional;

public interface DriverRepository {
    Driver save(Driver driver);
    Optional<Driver> findById(String id);
    Optional<Driver> findByPhone(String phone);
    List<Driver> findAll();
    List<Driver> findAvailableDrivers();
    List<Driver> findAvailableDriversByCarType(CarType carType);
    Optional<Driver> updateLocation(String driverId, Location location);
    Optional<Driver> updateStatus(String driverId, DriverStatus status);
    boolean tryAssignDriver(String driverId);
    void releaseDriver(String driverId);
    boolean existsById(String id);
    void clear();
}
