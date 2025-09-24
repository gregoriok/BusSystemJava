package com.example.BusSystem.service;

import com.example.BusSystem.domain.AbacatePay.AbacatePayWebhookDataDto;
import com.example.BusSystem.domain.AbacatePay.AbacatePayWebhookPayloadDto;
import com.example.BusSystem.domain.Line.Line;
import com.example.BusSystem.domain.Line.LineRepository;
import com.example.BusSystem.domain.Payment.AbacatePayPixResponseDataDto;
import com.example.BusSystem.domain.Ticket.*;
import com.example.BusSystem.domain.User.User;
import com.example.BusSystem.handler.BusTrackingHandler;
import com.example.BusSystem.service.exception.InactiveLineException;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class TicketService {

    @Autowired
    private TicketRepository ticketRepository;
    @Autowired
    private LineRepository lineRepository;
    @Autowired
    private PaymentService PaymentService;
    @Autowired
    private UserService userService;

    @Autowired
    private BusTrackingHandler BusTrackingHandler;
    public TicketPaymentResponseDto create(TicketDataInsert data, User user) {

        Line line = lineRepository.findById(data.lineId())
                .orElseThrow(() -> new EntityNotFoundException("Line not found"));

        if (!line.getActive()) {
            throw new InactiveLineException("Cannot buy ticket from a disabled line.");
        }

        Ticket ticket = new Ticket();
        ticket.setUser(user);
        ticket.setLine(line);
        ticket.setStatus(StatusEnum.PENDING);
        AbacatePayPixResponseDataDto AbacatePayResponse = null;
        try {
            ticketRepository.save(ticket);
            BigDecimal amount = new BigDecimal("15.00");
            try {
                AbacatePayResponse = PaymentService.createPíx(amount, ticket.getId(), user);

                var updateData = new TicketDataUpdate(null, AbacatePayResponse.id(), AbacatePayResponse.brCode());
                ticket.updateTicket(updateData);
                ticketRepository.save(ticket);
                TicketPaymentResponseDto TicketPaymentResponseDto = new TicketPaymentResponseDto(ticket, AbacatePayResponse);

            } catch (Exception e) {
                System.out.println(e);
            }
        } catch (Exception e) {
            System.out.println(e);
        }
        return new TicketPaymentResponseDto(ticket, AbacatePayResponse);
    }

    public Page<Ticket> list(Pageable pageable) {
        return ticketRepository.findAll(pageable);
    }

    public Page<Ticket> listByUser(User user,Pageable pageable) {
        return ticketRepository.findByUserId(user.getId(),pageable);
    }
    @Transactional
    public void processWebhookPaymentConfirmation(AbacatePayWebhookPayloadDto payload) {
        if (!"billing.paid".equalsIgnoreCase(payload.event())) {
            System.out.println("Evento ignorado: " + payload.event());
            return;
        }

        AbacatePayWebhookDataDto data = payload.data();
        if (data == null || data.pixQrCode() == null) {
            System.err.println("Webhook de pagamento recebido com dados malformados.");
            return;
        }

        String transactionId = data.pixQrCode().id();
        String paymentStatus = data.pixQrCode().status();

        if (!"PAID".equalsIgnoreCase(paymentStatus)) {
            System.out.println("Status do pagamento não é 'PAID', ignorando. Status: " + paymentStatus);
            return;
        }

        Ticket ticket = ticketRepository.findByPaymentTransactionId(transactionId)
                .orElse(null);

        if (ticket != null && ticket.getStatus() == StatusEnum.PENDING) {
            ticket.setStatus(StatusEnum.PAID);

            ticketRepository.save(ticket);

            String userId = ticket.getUser().getId().toString();
            String message = "{\"type\":\"PAYMENT_CONFIRMED\", \"ticketId\":\"" + ticket.getId() + "\"}";

            BusTrackingHandler.sendMessageToUser(userId, message);
        } else {
            if (ticket == null) {
                System.err.println("AVISO: Webhook recebido para a transação " + transactionId + ", mas nenhum ticket correspondente foi encontrado.");
            } else {
                System.out.println("INFO: Webhook recebido para o ticket " + ticket.getId() + ", que já estava com o status " + ticket.getStatus());
            }
        }
    }
}
