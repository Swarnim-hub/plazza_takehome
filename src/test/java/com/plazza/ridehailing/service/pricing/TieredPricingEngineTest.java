package com.plazza.ridehailing.service.pricing;

import com.plazza.ridehailing.model.CarType;
import com.plazza.ridehailing.model.Coupon;
import com.plazza.ridehailing.model.DiscountType;
import com.plazza.ridehailing.model.FareBreakdown;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Tiered Pricing Engine Unit Tests")
class TieredPricingEngineTest {

    private DefaultSurgePricingPolicy surgePricingPolicy;
    private TieredPricingEngine pricingEngine;

    @BeforeEach
    void setUp() {
        surgePricingPolicy = new DefaultSurgePricingPolicy();
        pricingEngine = new TieredPricingEngine(surgePricingPolicy);
    }

    @Test
    @DisplayName("Should accurately calculate tiered distance fare across all three tiers")
    void testTieredDistanceCalculation() {
        // Tier 1: 1.5 km -> 1.5 * 10 = 15.0
        assertEquals(15.0, pricingEngine.calculateTieredDistanceFare(1.5));

        // Exactly Tier 1 boundary: 2.0 km -> 2.0 * 10 = 20.0
        assertEquals(20.0, pricingEngine.calculateTieredDistanceFare(2.0));

        // Tier 2: 4.0 km -> 2 * 10 + 2 * 8 = 20 + 16 = 36.0
        assertEquals(36.0, pricingEngine.calculateTieredDistanceFare(4.0));

        // Exactly Tier 2 boundary: 5.0 km -> 2 * 10 + 3 * 8 = 20 + 24 = 44.0
        assertEquals(44.0, pricingEngine.calculateTieredDistanceFare(5.0));

        // Tier 3: 10.0 km -> 2 * 10 + 3 * 8 + 5 * 5 = 20 + 24 + 25 = 69.0
        assertEquals(69.0, pricingEngine.calculateTieredDistanceFare(10.0));
    }

    @Test
    @DisplayName("Should enforce minimum ride price of ₹50 when distance fare is lower")
    void testMinimumFareEnforcement() {
        // Distance 1 km: tiered fare is 10.0, but minimum fare for Hatchback is 50.0
        FareBreakdown fare = pricingEngine.calculateFare(1.0, CarType.HATCHBACK, CarType.HATCHBACK, null, 1.0);

        assertEquals(1.0, fare.getDistanceKm());
        assertEquals(10.0, fare.getTieredFare());
        assertEquals(50.0, fare.getMinimumFare());
        assertEquals(50.0, fare.getGrossFare());
        assertEquals(50.0, fare.getFinalFare());
        assertFalse(fare.isUpgradedFree());
    }

    @Test
    @DisplayName("Should differentiate pricing rates between Hatchback and Sedan")
    void testCarTypeRates() {
        double distance = 10.0; // Tiered base fare = 69.0

        // Hatchback: 69.0 * 1.0 = 69.0 (exceeds min 50.0)
        FareBreakdown hatchbackFare = pricingEngine.calculateFare(distance, CarType.HATCHBACK, CarType.HATCHBACK, null, 1.0);
        assertEquals(69.0, hatchbackFare.getFinalFare());
        assertEquals(1.0, hatchbackFare.getRateMultiplier());

        // Sedan: 69.0 * 1.5 = 103.5 (exceeds min 75.0)
        FareBreakdown sedanFare = pricingEngine.calculateFare(distance, CarType.SEDAN, CarType.SEDAN, null, 1.0);
        assertEquals(103.5, sedanFare.getFinalFare());
        assertEquals(1.5, sedanFare.getRateMultiplier());
        assertEquals(75.0, sedanFare.getMinimumFare());
    }

    @Test
    @DisplayName("Non-Negotiable: Upgrade Hatchback to Sedan at no extra cost (billed at Hatchback rate)")
    void testFreeUpgradeToSedan() {
        double distance = 10.0;

        // User requested HATCHBACK, but was assigned SEDAN
        FareBreakdown upgradedFare = pricingEngine.calculateFare(distance, CarType.HATCHBACK, CarType.SEDAN, null, 1.0);

        assertTrue(upgradedFare.isUpgradedFree(), "Should be marked as upgraded free");
        assertEquals(1.0, upgradedFare.getRateMultiplier(), "Rate multiplier must remain Hatchback rate (1.0)");
        assertEquals(50.0, upgradedFare.getMinimumFare(), "Minimum fare must remain Hatchback minimum (₹50)");
        assertEquals(69.0, upgradedFare.getFinalFare(), "Final fare must equal Hatchback price (69.0), NOT Sedan price (103.5)");
    }

    @Test
    @DisplayName("Should apply percentage coupon discount accurately")
    void testPercentageCouponApplication() {
        double distance = 10.0; // Gross fare for Hatchback = 69.0
        // 20% off, minimum fare ₹50, max discount ₹100
        Coupon coupon = new Coupon("SAVE20", DiscountType.PERCENTAGE, 20.0, 50.0, 100.0);

        FareBreakdown fare = pricingEngine.calculateFare(distance, CarType.HATCHBACK, CarType.HATCHBACK, coupon, 1.0);

        assertEquals("SAVE20", fare.getCouponCode());
        // 20% of 69.0 = 13.80
        assertEquals(13.80, fare.getDiscountAmount());
        // 69.0 - 13.80 = 55.20
        assertEquals(55.20, fare.getFinalFare());
    }

    @Test
    @DisplayName("Should apply flat amount coupon discount and respect max discount cap")
    void testFlatAmountCouponApplication() {
        double distance = 10.0; // Gross fare = 69.0
        Coupon coupon = new Coupon("FLAT30", DiscountType.FLAT_AMOUNT, 30.0, 50.0, 50.0);

        FareBreakdown fare = pricingEngine.calculateFare(distance, CarType.HATCHBACK, CarType.HATCHBACK, coupon, 1.0);

        assertEquals("FLAT30", fare.getCouponCode());
        assertEquals(30.0, fare.getDiscountAmount());
        // 69.0 - 30.0 = 39.0
        assertEquals(39.0, fare.getFinalFare());
    }

    @Test
    @DisplayName("Should not apply coupon if ride gross fare is below coupon's minimum fare requirement")
    void testCouponMinimumFareRequirement() {
        double distance = 1.0; // Gross fare = 50.0
        Coupon coupon = new Coupon("BIGRIDE", DiscountType.FLAT_AMOUNT, 50.0, 100.0, 50.0); // requires min 100

        FareBreakdown fare = pricingEngine.calculateFare(distance, CarType.HATCHBACK, CarType.HATCHBACK, coupon, 1.0);

        assertEquals(0.0, fare.getDiscountAmount());
        assertEquals(50.0, fare.getFinalFare());
    }

    @Test
    @DisplayName("Should apply surge pricing multiplier dynamically")
    void testSurgePricingMultiplier() {
        double distance = 10.0; // Base fare = 69.0
        double surge = 1.5;

        FareBreakdown fare = pricingEngine.calculateFare(distance, CarType.HATCHBACK, CarType.HATCHBACK, null, surge);

        assertEquals(1.5, fare.getSurgeMultiplier());
        // 69.0 * 1.5 = 103.5
        assertEquals(103.5, fare.getGrossFare());
        assertEquals(103.5, fare.getFinalFare());
    }
}
