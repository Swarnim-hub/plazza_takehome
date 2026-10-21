package com.plazza.ridehailing.service.matching;

import com.plazza.ridehailing.model.Driver;
import com.plazza.ridehailing.model.Location;

import java.util.List;

public interface DriverMatchingStrategy {
    MatchingStrategyType getType();

    /**
     * Orders candidate drivers based on strategy criteria (e.g. proximity or rating).
     */
    List<Driver> rankDrivers(List<Driver> candidates, Location pickupLocation);
}
