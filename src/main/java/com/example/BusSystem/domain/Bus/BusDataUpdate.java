package com.example.BusSystem.domain.Bus;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.UUID;

public record BusDataUpdate(
        @JsonProperty("licensePlate")
        String licensePlate,
        @JsonProperty("capacity")
        Integer capacity,
        @JsonProperty("number")
        Integer number,
        @JsonProperty("type")
        BusTypeEnum type
) {
}
