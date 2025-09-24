package com.example.BusSystem.domain.Payment;

public record AbacatePayPixRequestDto( int amount, // centavos
                                       int expiresIn, // segundos
                                       String description,
                                       PixPaymentCustomerDto customer) {
}
