package com.plazza.ridehailing.service.pricing;

import com.plazza.ridehailing.model.CarType;
import com.plazza.ridehailing.model.Coupon;
import com.plazza.ridehailing.model.FareBreakdown;
import org.springframework.stereotype.Service;

@Service
public class TieredPricingEngine implements PricingEngine {

    public static final double TIER_1_MAX_KM = 2.0;
    public static final double TIER_1_RATE_PER_KM = 10.0;

    public static final double TIER_2_MAX_KM = 5.0;
    public static final double TIER_2_RATE_PER_KM = 8.0;

    public static final double TIER_3_RATE_PER_KM = 5.0;

    private final SurgePricingPolicy surgePricingPolicy;

    public TieredPricingEngine(SurgePricingPolicy surgePricingPolicy) {
        this.surgePricingPolicy = surgePricingPolicy;
    }

    @Override
    public FareBreakdown calculateFare(double distanceKm, CarType requestedCarType, CarType actualCarType,
                                        Coupon coupon, double surgeMultiplier) {
        if (distanceKm < 0) {
            distanceKm = 0.0;
        }

        // 1. Calculate base tiered distance fare
        double tieredFare = calculateTieredDistanceFare(distanceKm);

        // 2. Determine applicable car type rate:
        // Free Upgrade Rule: If user requested Hatchback but got Sedan, charge Hatchback rate!
        boolean isFreeUpgrade = (requestedCarType == CarType.HATCHBACK && actualCarType == CarType.SEDAN);
        CarType billingCarType = isFreeUpgrade ? requestedCarType : (requestedCarType != null ? requestedCarType : actualCarType);
        if (billingCarType == null) {
            billingCarType = CarType.HATCHBACK;
        }

        double rateMultiplier = billingCarType.getRateMultiplier();
        double minimumFare = billingCarType.getMinimumFare();

        double distanceFareAfterMultiplier = tieredFare * rateMultiplier;

        // 3. Apply surge multiplier
        if (surgeMultiplier < 1.0) {
            surgeMultiplier = 1.0;
        }
        double surgedFare = distanceFareAfterMultiplier * surgeMultiplier;

        // 4. Apply minimum fare threshold
        double grossFare = Math.max(surgedFare, minimumFare);
        grossFare = round(grossFare);

        // 5. Apply coupon if valid
        double discountAmount = 0.0;
        String couponCode = null;
        if (coupon != null && coupon.isActive()) {
            couponCode = coupon.getCode();
            discountAmount = coupon.calculateDiscount(grossFare);
        }

        double finalFare = Math.max(0.0, grossFare - discountAmount);
        finalFare = round(finalFare);

        return new FareBreakdown(
                round(distanceKm),
                round(tieredFare),
                rateMultiplier,
                minimumFare,
                isFreeUpgrade,
                surgeMultiplier,
                grossFare,
                couponCode,
                discountAmount,
                0.0,
                finalFare
        );
    }

    @Override
    public FareBreakdown calculateFare(double distanceKm, CarType requestedCarType, CarType actualCarType,
                                        Coupon coupon) {
        double surge = surgePricingPolicy.getSurgeMultiplier(null, requestedCarType);
        return calculateFare(distanceKm, requestedCarType, actualCarType, coupon, surge);
    }

    /**
     * Tiered pricing calculation:
     * - First 2 km: ₹10/km
     * - 3-5 km (next 3 km): ₹8/km
     * - 6 km and beyond: ₹5/km
     */
    public double calculateTieredDistanceFare(double distanceKm) {
        if (distanceKm <= 0.0) {
            return 0.0;
        }

        double fare = 0.0;

        // Tier 1: 0 - 2 km
        double t1Distance = Math.min(distanceKm, TIER_1_MAX_KM);
        fare += t1Distance * TIER_1_RATE_PER_KM;

        // Tier 2: 2 - 5 km (up to 3 km)
        if (distanceKm > TIER_1_MAX_KM) {
            double t2Distance = Math.min(distanceKm, TIER_2_MAX_KM) - TIER_1_MAX_KM;
            fare += t2Distance * TIER_2_RATE_PER_KM;
        }

        // Tier 3: beyond 5 km
        if (distanceKm > TIER_2_MAX_KM) {
            double t3Distance = distanceKm - TIER_2_MAX_KM;
            fare += t3Distance * TIER_3_RATE_PER_KM;
        }

        return round(fare);
    }

    private double round(double value) {
        return Math.round(value * 100.0) / 100.0;
    }
}
