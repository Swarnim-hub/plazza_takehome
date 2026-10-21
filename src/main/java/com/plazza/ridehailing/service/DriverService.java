package com.plazza.ridehailing.service;

import com.plazza.ridehailing.dto.request.RegisterDriverRequest;
import com.plazza.ridehailing.dto.request.UpdateLocationRequest;
import com.plazza.ridehailing.dto.response.DriverResponse;
import com.plazza.ridehailing.exception.ConflictException;
import com.plazza.ridehailing.exception.ResourceNotFoundException;
import com.plazza.ridehailing.model.CarType;
import com.plazza.ridehailing.model.Driver;
import com.plazza.ridehailing.model.DriverStatus;
import com.plazza.ridehailing.model.Location;
import com.plazza.ridehailing.repository.DriverRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DriverService {
    private final DriverRepository driverRepository;

    public DriverService(DriverRepository driverRepository) {
        this.driverRepository = driverRepository;
    }

    public DriverResponse registerDriver(RegisterDriverRequest request) {
        if (driverRepository.findByPhone(request.getPhone()).isPresent()) {
            throw new ConflictException("Driver already registered with phone: " + request.getPhone());
        }

        double rating = (request.getRating() != null && request.getRating() >= 1.0 && request.getRating() <= 5.0)
                ? request.getRating() : 5.0;

        Driver driver = new Driver(
                null,
                request.getName(),
                request.getPhone(),
                request.getCarType(),
                request.getVehicleNumber(),
                request.getLocation(),
                rating
        );

        Driver saved = driverRepository.save(driver);
        return DriverResponse.from(saved);
    }

    public DriverResponse getDriverById(String id) {
        Driver driver = driverRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Driver not found with id: " + id));
        return DriverResponse.from(driver);
    }

    public List<DriverResponse> getAllDrivers() {
        return driverRepository.findAll().stream()
                .map(DriverResponse::from)
                .toList();
    }

    public List<DriverResponse> getAvailableDrivers(CarType carType) {
        List<Driver> drivers = (carType != null)
                ? driverRepository.findAvailableDriversByCarType(carType)
                : driverRepository.findAvailableDrivers();
        return drivers.stream()
                .map(DriverResponse::from)
                .toList();
    }

    public DriverResponse updateDriverLocation(String id, UpdateLocationRequest request) {
        Location newLocation = new Location(request.getLatitude(), request.getLongitude());
        Driver updated = driverRepository.updateLocation(id, newLocation)
                .orElseThrow(() -> new ResourceNotFoundException("Driver not found with id: " + id));
        return DriverResponse.from(updated);
    }

    public DriverResponse updateDriverStatus(String id, DriverStatus status) {
        Driver updated = driverRepository.updateStatus(id, status)
                .orElseThrow(() -> new ResourceNotFoundException("Driver not found with id: " + id));
        return DriverResponse.from(updated);
    }
}
