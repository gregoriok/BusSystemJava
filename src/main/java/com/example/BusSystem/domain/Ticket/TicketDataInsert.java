package com.example.BusSystem.domain.Ticket;

import java.util.UUID;

public record TicketDataInsert(
        UUID userId,
        UUID lineId
) {
}
