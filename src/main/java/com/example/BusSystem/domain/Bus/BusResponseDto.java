package com.example.BusSystem.domain.Bus;

import java.util.UUID;

public record BusResponseDto(UUID Id, int number, String licensePlate, int capacity) {
    public BusResponseDto(Bus bus){
        this(bus.getId(), bus.getNumber(), bus.getLicensePlate(), bus.getCapacity());
    }
}
