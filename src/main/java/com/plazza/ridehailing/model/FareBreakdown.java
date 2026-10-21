package com.plazza.ridehailing.model;

public class FareBreakdown {
    private double distanceKm;
    private double tieredFare;
    private double rateMultiplier;
    private double minimumFare;
    private boolean upgradedFree;
    private double surgeMultiplier;
    private double grossFare;
    private String couponCode;
    private double discountAmount;
    private double cancellationFee;
    private double finalFare;

    public FareBreakdown() {
        this.surgeMultiplier = 1.0;
        this.rateMultiplier = 1.0;
    }

    public FareBreakdown(double distanceKm, double tieredFare, double rateMultiplier, double minimumFare,
                         boolean upgradedFree, double surgeMultiplier, double grossFare,
                         String couponCode, double discountAmount, double cancellationFee, double finalFare) {
        this.distanceKm = distanceKm;
        this.tieredFare = tieredFare;
        this.rateMultiplier = rateMultiplier;
        this.minimumFare = minimumFare;
        this.upgradedFree = upgradedFree;
        this.surgeMultiplier = surgeMultiplier;
        this.grossFare = grossFare;
        this.couponCode = couponCode;
        this.discountAmount = discountAmount;
        this.cancellationFee = cancellationFee;
        this.finalFare = finalFare;
    }

    public double getDistanceKm() {
        return distanceKm;
    }

    public void setDistanceKm(double distanceKm) {
        this.distanceKm = distanceKm;
    }

    public double getTieredFare() {
        return tieredFare;
    }

    public void setTieredFare(double tieredFare) {
        this.tieredFare = tieredFare;
    }

    public double getRateMultiplier() {
        return rateMultiplier;
    }

    public void setRateMultiplier(double rateMultiplier) {
        this.rateMultiplier = rateMultiplier;
    }

    public double getMinimumFare() {
        return minimumFare;
    }

    public void setMinimumFare(double minimumFare) {
        this.minimumFare = minimumFare;
    }

    public boolean isUpgradedFree() {
        return upgradedFree;
    }

    public void setUpgradedFree(boolean upgradedFree) {
        this.upgradedFree = upgradedFree;
    }

    public double getSurgeMultiplier() {
        return surgeMultiplier;
    }

    public void setSurgeMultiplier(double surgeMultiplier) {
        this.surgeMultiplier = surgeMultiplier;
    }

    public double getGrossFare() {
        return grossFare;
    }

    public void setGrossFare(double grossFare) {
        this.grossFare = grossFare;
    }

    public String getCouponCode() {
        return couponCode;
    }

    public void setCouponCode(String couponCode) {
        this.couponCode = couponCode;
    }

    public double getDiscountAmount() {
        return discountAmount;
    }

    public void setDiscountAmount(double discountAmount) {
        this.discountAmount = discountAmount;
    }

    public double getCancellationFee() {
        return cancellationFee;
    }

    public void setCancellationFee(double cancellationFee) {
        this.cancellationFee = cancellationFee;
    }

    public double getFinalFare() {
        return finalFare;
    }

    public void setFinalFare(double finalFare) {
        this.finalFare = finalFare;
    }
}
