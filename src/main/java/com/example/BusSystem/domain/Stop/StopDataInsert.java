package com.example.BusSystem.domain.Stop;

public record StopDataInsert(
        String name,
        Double latitude,
        Double longitude,
        String description
) {
}
