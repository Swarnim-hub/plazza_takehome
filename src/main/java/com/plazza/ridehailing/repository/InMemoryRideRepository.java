package com.plazza.ridehailing.repository;

import com.plazza.ridehailing.model.Ride;
import com.plazza.ridehailing.model.RideStatus;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class InMemoryRideRepository implements RideRepository {
    private final Map<String, Ride> store = new ConcurrentHashMap<>();

    @Override
    public Ride save(Ride ride) {
        if (ride.getId() == null) {
            ride.setId("RIDE-" + System.currentTimeMillis() + "-" + (int)(Math.random() * 1000));
        }
        store.put(ride.getId(), ride);
        return ride;
    }

    @Override
    public Optional<Ride> findById(String id) {
        if (id == null) return Optional.empty();
        return Optional.ofNullable(store.get(id));
    }

    @Override
    public List<Ride> findAll() {
        return store.values().stream()
                .sorted(Comparator.comparing(Ride::getCreatedAt).reversed())
                .toList();
    }

    @Override
    public List<Ride> findByUserId(String userId) {
        if (userId == null) return List.of();
        return store.values().stream()
                .filter(r -> userId.equals(r.getUserId()))
                .sorted(Comparator.comparing(Ride::getCreatedAt).reversed())
                .toList();
    }

    @Override
    public List<Ride> findByDriverId(String driverId) {
        if (driverId == null) return List.of();
        return store.values().stream()
                .filter(r -> driverId.equals(r.getDriverId()))
                .sorted(Comparator.comparing(Ride::getCreatedAt).reversed())
                .toList();
    }

    @Override
    public List<Ride> findByUserIdAndStatusIn(String userId, List<RideStatus> statuses) {
        if (userId == null || statuses == null) return List.of();
        return store.values().stream()
                .filter(r -> userId.equals(r.getUserId()) && statuses.contains(r.getStatus()))
                .sorted(Comparator.comparing(Ride::getCreatedAt).reversed())
                .toList();
    }

    @Override
    public List<Ride> findByDriverIdAndStatusIn(String driverId, List<RideStatus> statuses) {
        if (driverId == null || statuses == null) return List.of();
        return store.values().stream()
                .filter(r -> driverId.equals(r.getDriverId()) && statuses.contains(r.getStatus()))
                .sorted(Comparator.comparing(Ride::getCreatedAt).reversed())
                .toList();
    }

    @Override
    public Optional<Ride> findActiveRideByUserId(String userId) {
        if (userId == null) return Optional.empty();
        return store.values().stream()
                .filter(r -> userId.equals(r.getUserId()) &&
                        (r.getStatus() == RideStatus.BOOKED || r.getStatus() == RideStatus.ONGOING))
                .findFirst();
    }

    @Override
    public Optional<Ride> findActiveRideByDriverId(String driverId) {
        if (driverId == null) return Optional.empty();
        return store.values().stream()
                .filter(r -> driverId.equals(r.getDriverId()) &&
                        (r.getStatus() == RideStatus.BOOKED || r.getStatus() == RideStatus.ONGOING))
                .findFirst();
    }

    @Override
    public void clear() {
        store.clear();
    }
}
