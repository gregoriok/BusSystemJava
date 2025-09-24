package com.example.BusSystem.controller;

import com.example.BusSystem.domain.Ticket.TicketDataInsert;
import com.example.BusSystem.domain.Ticket.TicketPaymentResponseDto;
import com.example.BusSystem.domain.Ticket.TicketResponseDto;
import com.example.BusSystem.service.TicketService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.example.BusSystem.domain.User.User;
import org.springframework.security.core.Authentication;
import java.security.Principal;


@RestController
@RequestMapping("/api/v1/tickets")
public class TicketController {

    @Autowired
    private TicketService ticketService;
    @PostMapping
    public ResponseEntity<TicketPaymentResponseDto> buy(@RequestBody @Valid TicketDataInsert data, Principal principal){
        User user = (User) ((Authentication) principal).getPrincipal();
        TicketPaymentResponseDto TicketPaymentResponseDto = ticketService.create(data, user);
        return ResponseEntity.status(HttpStatus.CREATED).body(TicketPaymentResponseDto);
    }
    @GetMapping
    public ResponseEntity<Page<TicketResponseDto>> list(Pageable pageable, Principal principal){
        User user = (User) ((Authentication) principal).getPrincipal();
        var page = ticketService.listByUser(user, pageable).map(TicketResponseDto::new);
        return ResponseEntity.status(HttpStatus.OK).body(page);
    }
}
