package com.example.BusSystem.domain.Stop;

import com.example.BusSystem.domain.Stop.Stop;

import java.util.UUID;

public record StopsResponseDto(UUID Id, String name, Double latitude, Double longitude, String description) {
    public StopsResponseDto(Stop stop){
        this(stop.getId(),stop.getName(),stop.getLatitude(),stop.getLongitude(),stop.getDescription());
    }
}
