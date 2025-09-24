package com.example.BusSystem.domain.LineStop;

import java.util.UUID;

public record LineStopDataInsert(
        UUID stopId,
        int sequence
) {
}
