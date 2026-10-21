package com.plazza.ridehailing.dto.request;

import com.plazza.ridehailing.model.DiscountType;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class AddCouponRequest {
    @NotBlank(message = "Coupon code cannot be blank")
    private String code;

    @NotNull(message = "Discount type is required (PERCENTAGE or FLAT_AMOUNT)")
    private DiscountType discountType;

    @NotNull(message = "Discount value is required")
    @DecimalMin(value = "0.01", message = "Discount value must be greater than 0")
    private Double discountValue;

    private Double minimumFare = 50.0;

    private Double maxDiscount = Double.MAX_VALUE;

    public AddCouponRequest() {
    }

    public AddCouponRequest(String code, DiscountType discountType, Double discountValue,
                            Double minimumFare, Double maxDiscount) {
        this.code = code;
        this.discountType = discountType;
        this.discountValue = discountValue;
        this.minimumFare = minimumFare != null ? minimumFare : 50.0;
        this.maxDiscount = maxDiscount != null ? maxDiscount : Double.MAX_VALUE;
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

    public Double getDiscountValue() {
        return discountValue;
    }

    public void setDiscountValue(Double discountValue) {
        this.discountValue = discountValue;
    }

    public Double getMinimumFare() {
        return minimumFare;
    }

    public void setMinimumFare(Double minimumFare) {
        this.minimumFare = minimumFare;
    }

    public Double getMaxDiscount() {
        return maxDiscount;
    }

    public void setMaxDiscount(Double maxDiscount) {
        this.maxDiscount = maxDiscount;
    }
}
