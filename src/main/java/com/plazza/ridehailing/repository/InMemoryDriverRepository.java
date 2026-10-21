package com.plazza.ridehailing.repository;

import com.plazza.ridehailing.model.CarType;
import com.plazza.ridehailing.model.Driver;
import com.plazza.ridehailing.model.DriverStatus;
import com.plazza.ridehailing.model.Location;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class InMemoryDriverRepository implements DriverRepository {
    private final Map<String, Driver> store = new ConcurrentHashMap<>();
    private final Object lock = new Object();

    @Override
    public Driver save(Driver driver) {
        if (driver.getId() == null) {
            driver.setId("DRV-" + System.currentTimeMillis() + "-" + (int)(Math.random() * 1000));
        }
        store.put(driver.getId(), driver);
        return driver;
    }

    @Override
    public Optional<Driver> findById(String id) {
        if (id == null) return Optional.empty();
        return Optional.ofNullable(store.get(id));
    }

    @Override
    public Optional<Driver> findByPhone(String phone) {
        if (phone == null) return Optional.empty();
        return store.values().stream()
                .filter(d -> phone.equalsIgnoreCase(d.getPhone()))
                .findFirst();
    }

    @Override
    public List<Driver> findAll() {
        return new ArrayList<>(store.values());
    }

    @Override
    public List<Driver> findAvailableDrivers() {
        return store.values().stream()
                .filter(Driver::isAvailable)
                .toList();
    }

    @Override
    public List<Driver> findAvailableDriversByCarType(CarType carType) {
        return store.values().stream()
                .filter(d -> d.isAvailable() && d.getCarType() == carType)
                .toList();
    }

    @Override
    public Optional<Driver> updateLocation(String driverId, Location location) {
        Driver driver = store.get(driverId);
        if (driver != null) {
            driver.setCurrentLocation(location);
            return Optional.of(driver);
        }
        return Optional.empty();
    }

    @Override
    public Optional<Driver> updateStatus(String driverId, DriverStatus status) {
        synchronized (lock) {
            Driver driver = store.get(driverId);
            if (driver != null) {
                driver.setStatus(status);
                return Optional.of(driver);
            }
            return Optional.empty();
        }
    }

    @Override
    public boolean tryAssignDriver(String driverId) {
        synchronized (lock) {
            Driver driver = store.get(driverId);
            if (driver != null && driver.isAvailable()) {
                driver.setStatus(DriverStatus.ON_RIDE);
                return true;
            }
            return false;
        }
    }

    @Override
    public void releaseDriver(String driverId) {
        synchronized (lock) {
            Driver driver = store.get(driverId);
            if (driver != null) {
                driver.setStatus(DriverStatus.AVAILABLE);
            }
        }
    }

    @Override
    public boolean existsById(String id) {
        return id != null && store.containsKey(id);
    }

    @Override
    public void clear() {
        store.clear();
    }
}
