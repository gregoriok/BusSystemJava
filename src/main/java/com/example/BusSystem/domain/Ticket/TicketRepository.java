package com.example.BusSystem.domain.Ticket;

import com.example.BusSystem.domain.User.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface TicketRepository extends JpaRepository<Ticket, UUID> {
    Optional<Ticket> findByPaymentTransactionId(String transactionId);

    Page<Ticket> findByUserId(UUID userId, Pageable pageable);
}
