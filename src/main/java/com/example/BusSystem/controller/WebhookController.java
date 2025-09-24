package com.example.BusSystem.controller;

import com.example.BusSystem.domain.AbacatePay.AbacatePayWebhookPayloadDto;
import com.example.BusSystem.service.TicketService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/webhooks")
public class WebhookController {

    @Autowired
    private TicketService ticketService;

    @PostMapping("/abacatepay")
    public ResponseEntity<Void> handleAbacatePayWebhook(@RequestBody AbacatePayWebhookPayloadDto payload) {
        ticketService.processWebhookPaymentConfirmation(payload);
        return ResponseEntity.ok().build();
    }
}
