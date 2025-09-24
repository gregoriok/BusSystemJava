package com.example.BusSystem.domain.Stop;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.UUID;

public record StopDataUpdate(
        @JsonProperty("name")
        String name,
        @JsonProperty("latitude")
        Double  latitude,
        @JsonProperty("longitude")
        Double  longitude,
        @JsonProperty("description")
        String description
) {
}
