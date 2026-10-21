package com.plazza.ridehailing.service.pricing;

import com.plazza.ridehailing.model.CarType;
import com.plazza.ridehailing.model.Location;
import org.springframework.stereotype.Component;

import java.util.concurrent.atomic.AtomicReference;

@Component
public class DefaultSurgePricingPolicy implements SurgePricingPolicy {
    private final AtomicReference<Double> currentSurgeMultiplier = new AtomicReference<>(1.0);

    @Override
    public double getSurgeMultiplier(Location pickupLocation, CarType carType) {
        return currentSurgeMultiplier.get();
    }

    @Override
    public void setSurgeMultiplier(double multiplier) {
        if (multiplier < 1.0) {
            multiplier = 1.0;
        }
        currentSurgeMultiplier.set(multiplier);
    }
}
