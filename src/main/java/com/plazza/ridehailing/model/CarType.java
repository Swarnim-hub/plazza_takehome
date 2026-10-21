package com.plazza.ridehailing.model;

public enum CarType {
    HATCHBACK(1.0, 50.0),
    SEDAN(1.5, 75.0);

    private final double rateMultiplier;
    private final double minimumFare;

    CarType(double rateMultiplier, double minimumFare) {
        this.rateMultiplier = rateMultiplier;
        this.minimumFare = minimumFare;
    }

    public double getRateMultiplier() {
        return rateMultiplier;
    }

    public double getMinimumFare() {
        return minimumFare;
    }
}
