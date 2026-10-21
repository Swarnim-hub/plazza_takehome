package com.plazza.ridehailing.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

public class SetSurgeRequest {
    @NotNull(message = "Surge multiplier cannot be null")
    @DecimalMin(value = "1.0", message = "Surge multiplier must be at least 1.0")
    private Double multiplier;

    public SetSurgeRequest() {
    }

    public SetSurgeRequest(Double multiplier) {
        this.multiplier = multiplier;
    }

    public Double getMultiplier() {
        return multiplier;
    }

    public void setMultiplier(Double multiplier) {
        this.multiplier = multiplier;
    }
}
