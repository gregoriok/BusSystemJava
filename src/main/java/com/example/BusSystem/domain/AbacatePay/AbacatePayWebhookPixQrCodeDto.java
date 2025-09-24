package com.example.BusSystem.domain.AbacatePay;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record AbacatePayWebhookPixQrCodeDto( String id,
                                             String status) {
}
