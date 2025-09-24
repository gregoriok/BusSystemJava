package com.example.BusSystem.domain.LineStop;

import java.util.UUID;

public record LineStopResponseDto(UUID lineId, UUID stopId, int sequence, String lineName, String stopName) {
    public LineStopResponseDto(LineStop lineStop) {
        this(lineStop.getLine().getId(), lineStop.getStop().getId(), lineStop.getSequence(), lineStop.getLine().getName(), lineStop.getStop().getName());
    }
}
