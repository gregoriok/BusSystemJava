package com.example.BusSystem.domain.Payment;

import java.util.UUID;

public record InitiatePurchaseResponseDto(UUID ticketId,
                                          String pixCopyPaste,
                                          String qrCodeImageBase64) {
}
