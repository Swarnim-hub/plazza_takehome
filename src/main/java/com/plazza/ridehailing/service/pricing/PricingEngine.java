package com.plazza.ridehailing.service.pricing;

import com.plazza.ridehailing.model.CarType;
import com.plazza.ridehailing.model.Coupon;
import com.plazza.ridehailing.model.FareBreakdown;

public interface PricingEngine {
    FareBreakdown calculateFare(double distanceKm, CarType requestedCarType, CarType actualCarType,
                                 Coupon coupon, double surgeMultiplier);

    FareBreakdown calculateFare(double distanceKm, CarType requestedCarType, CarType actualCarType,
                                 Coupon coupon);
}
