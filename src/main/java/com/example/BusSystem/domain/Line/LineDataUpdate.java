package com.example.BusSystem.domain.Line;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.UUID;

public record LineDataUpdate(
        @JsonProperty("name")
        String name,
        @JsonProperty("description")
        String description,
        @JsonProperty("active")
        Boolean active
) {
}
