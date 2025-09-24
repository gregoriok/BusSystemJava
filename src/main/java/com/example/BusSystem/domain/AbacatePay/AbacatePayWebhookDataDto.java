package com.example.BusSystem.domain.AbacatePay;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record AbacatePayWebhookDataDto(AbacatePayWebhookPaymentDto payment,
                                       AbacatePayWebhookPixQrCodeDto pixQrCode) {
}
