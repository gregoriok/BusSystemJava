package com.example.BusSystem.domain.Ticket;

public record TicketDataUpdate(
        StatusEnum status,
        String paymentTransactionId,
        String qrCodeHash
) {
}
