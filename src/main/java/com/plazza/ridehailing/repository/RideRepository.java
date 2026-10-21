package com.plazza.ridehailing.repository;

import com.plazza.ridehailing.model.Ride;
import com.plazza.ridehailing.model.RideStatus;

import java.util.List;
import java.util.Optional;

public interface RideRepository {
    Ride save(Ride ride);
    Optional<Ride> findById(String id);
    List<Ride> findAll();
    List<Ride> findByUserId(String userId);
    List<Ride> findByDriverId(String driverId);
    List<Ride> findByUserIdAndStatusIn(String userId, List<RideStatus> statuses);
    List<Ride> findByDriverIdAndStatusIn(String driverId, List<RideStatus> statuses);
    Optional<Ride> findActiveRideByUserId(String userId);
    Optional<Ride> findActiveRideByDriverId(String driverId);
    void clear();
}
