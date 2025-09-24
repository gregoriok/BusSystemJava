package com.example.BusSystem.domain.Ticket;

import java.util.UUID;

public record TicketResponseDto(UUID Id,UUID lineId,String lineName, StatusEnum status){
    public TicketResponseDto(Ticket ticket){
        this(ticket.getId(),ticket.getLine().getId(),ticket.getLine().getName(),ticket.getStatus());
    }
}
