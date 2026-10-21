package com.plazza.ridehailing.dto.response;

import com.plazza.ridehailing.model.Coupon;
import com.plazza.ridehailing.model.DiscountType;

import java.time.Instant;

public class CouponResponse {
    private String code;
    private DiscountType discountType;
    private double discountValue;
    private double minimumFare;
    private double maxDiscount;
    private boolean active;
    private Instant createdAt;

    public CouponResponse() {
    }

    public static CouponResponse from(Coupon coupon) {
        if (coupon == null) return null;
        CouponResponse response = new CouponResponse();
        response.setCode(coupon.getCode());
        response.setDiscountType(coupon.getDiscountType());
        response.setDiscountValue(coupon.getDiscountValue());
        response.setMinimumFare(coupon.getMinimumFare());
        response.setMaxDiscount(coupon.getMaxDiscount());
        response.setActive(coupon.isActive());
        response.setCreatedAt(coupon.getCreatedAt());
        return response;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
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
}
