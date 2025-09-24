package com.example.BusSystem.domain.Bus;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record BusDataInsert(
        String licensePlate,
        int capacity,

        int number,

        @NotNull
        BusTypeEnum type

) {
}
