package com.plazza.ridehailing.service.matching;

import com.plazza.ridehailing.model.Driver;
import com.plazza.ridehailing.model.Location;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.List;

@Component
public class HighestRatedDriverMatchingStrategy implements DriverMatchingStrategy {

    @Override
    public MatchingStrategyType getType() {
        return MatchingStrategyType.HIGHEST_RATED;
    }

    @Override
    public List<Driver> rankDrivers(List<Driver> candidates, Location pickupLocation) {
        if (candidates == null || candidates.isEmpty()) {
            return List.of();
        }

        return candidates.stream()
                .sorted(Comparator
                        .comparingDouble(Driver::getRating).reversed()
                        .thenComparingDouble(d -> d.getCurrentLocation().distanceTo(pickupLocation)))
                .toList();
    }
}
