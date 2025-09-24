package com.example.BusSystem.domain.Bus;

import com.fasterxml.jackson.annotation.JsonProperty;

public record BusLocalizationUpdate(
        @JsonProperty("currentLatitude")
        Double currentLatitude,
        @JsonProperty("currentLongitude")
        Double currentLongitude
) {
}
