package com.example.BusSystem.domain.Bus;

public record BusLocalizationResponseDto(Double currentLatitude, Double currentLongitude) {
    public BusLocalizationResponseDto(Bus bus){
        this(bus.getCurrentLatitude(),bus.getCurrentLongitude());
    }
}
