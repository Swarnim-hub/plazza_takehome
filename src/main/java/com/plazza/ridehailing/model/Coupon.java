package com.plazza.ridehailing.model;

import java.time.Instant;

public class Coupon {
    private String code;
    private DiscountType discountType;
    private double discountValue;
    private double minimumFare;
    private double maxDiscount;
    private boolean active;
    private Instant createdAt;

    public Coupon() {
        this.active = true;
        this.maxDiscount = Double.MAX_VALUE;
        this.createdAt = Instant.now();
    }

    public Coupon(String code, DiscountType discountType, double discountValue, double minimumFare, double maxDiscount) {
        this.code = code != null ? code.toUpperCase().trim() : null;
        this.discountType = discountType;
        this.discountValue = discountValue;
        this.minimumFare = minimumFare;
        this.maxDiscount = maxDiscount > 0 ? maxDiscount : Double.MAX_VALUE;
        this.active = true;
        this.createdAt = Instant.now();
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code != null ? code.toUpperCase().trim() : null;
    }

    public DiscountType getDiscountType() {
        return discountType;
    }

    public void setDiscountType(DiscountType discountType) {
        this.discountType = discountType;
    }

    public double getDiscountValue() {
        return discountValue;
    }

    public void setDiscountValue(double discountValue) {
        this.discountValue = discountValue;
    }

    public double getMinimumFare() {
        return minimumFare;
    }

    public void setMinimumFare(double minimumFare) {
        this.minimumFare = minimumFare;
    }

    public double getMaxDiscount() {
        return maxDiscount;
    }

    public void setMaxDiscount(double maxDiscount) {
        this.maxDiscount = maxDiscount;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    /**
     * Calculates discount for a given gross fare.
     */
    public double calculateDiscount(double grossFare) {
        if (!active || grossFare < minimumFare) {
            return 0.0;
        }

        double discount = 0.0;
        if (discountType == DiscountType.PERCENTAGE) {
            discount = (grossFare * discountValue) / 100.0;
        } else if (discountType == DiscountType.FLAT_AMOUNT) {
            discount = discountValue;
        }

        discount = Math.min(discount, maxDiscount);
        discount = Math.min(discount, grossFare); // Can't exceed total fare
        return Math.round(discount * 100.0) / 100.0;
    }
}
