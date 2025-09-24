package com.example.BusSystem.domain.Ticket;

import com.example.BusSystem.domain.Line.Line;
import com.example.BusSystem.domain.User.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

@Table(name = "tickets")
@Entity(name = "Ticket")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
public class Ticket {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "line_id", nullable = false)
    private Line line;


    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatusEnum status;

    @Column(unique = true)
    private String paymentTransactionId;

    @Column(name = "qr_code_hash", unique = true)
    private String qrCodeHash;


    public void updateTicket(TicketDataUpdate data) {
        if (data.status() != null) {
            this.setStatus(data.status());
        }
        if (data.paymentTransactionId() != null) {
            this.setPaymentTransactionId(data.paymentTransactionId());
        }
        if (data.qrCodeHash() != null) {
            this.setQrCodeHash(data.qrCodeHash());
        }
    }
}