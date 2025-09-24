package com.example.BusSystem.domain.Payment;

public record PixPaymentCustomerDto(String name,
                                    String cellphone,
                                    String email,
                                    String taxId) {
}
