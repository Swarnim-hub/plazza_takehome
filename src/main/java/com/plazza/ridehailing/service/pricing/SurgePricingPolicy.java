package com.plazza.ridehailing.service.pricing;

import com.plazza.ridehailing.model.CarType;
import com.plazza.ridehailing.model.Location;

public interface SurgePricingPolicy {
    double getSurgeMultiplier(Location pickupLocation, CarType carType);
    void setSurgeMultiplier(double multiplier);
}
