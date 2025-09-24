package com.example.BusSystem.domain.Ticket;

import com.example.BusSystem.domain.Payment.AbacatePayPixResponseDataDto;

import java.util.UUID;

public record TicketPaymentResponseDto(UUID Id, UUID userId, UUID lineId, String qrCodeHash, String qrCodeBase64) {
    public TicketPaymentResponseDto(Ticket ticket, AbacatePayPixResponseDataDto paymentresponse){
        this(ticket.getId(),ticket.getUser().getId(),ticket.getLine().getId(), ticket.getQrCodeHash(),paymentresponse.brCodeBase64());
    }
}
