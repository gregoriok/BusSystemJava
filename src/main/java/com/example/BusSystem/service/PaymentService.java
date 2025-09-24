package com.example.BusSystem.service;

import com.example.BusSystem.domain.Payment.AbacatePayPixRequestDto;
import com.example.BusSystem.domain.Payment.AbacatePayPixResponseDataDto;
import com.example.BusSystem.domain.Payment.AbacatePayPixResponseWrapperDto;
import com.example.BusSystem.domain.Payment.PixPaymentCustomerDto;
import com.example.BusSystem.domain.User.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.UUID;

@Service
public class PaymentService {

    @Autowired
    private RestTemplate restTemplate;

    @Value("${abacatepay.api.url}")
    private String apiUrl;

    @Value("${abacatepay.api.key}")
    private String apiKey;

    public AbacatePayPixResponseDataDto createPíx(BigDecimal amount, UUID externalId, User user){
        String endpoint = apiUrl + "/pixQrCode/create";
        PixPaymentCustomerDto customer = new PixPaymentCustomerDto(
                "teste",
                "999999999",
                user.getEmail(),
                "02936897092"
        );
        int amountInCents = amount.multiply(new BigDecimal("100")).intValue();
        AbacatePayPixRequestDto requestBody = new AbacatePayPixRequestDto(
                amountInCents,
                3600, // Expira em 3600 segundos (1 hora)
                "Passagem de ônibus - Ticket ID: " + externalId,
                customer
        );
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + apiKey);
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<AbacatePayPixRequestDto> requestEntity = new HttpEntity<>(requestBody, headers);
        try {
            ResponseEntity<AbacatePayPixResponseWrapperDto> response = restTemplate.postForEntity(
                    endpoint,
                    requestEntity,
                    AbacatePayPixResponseWrapperDto.class
            );
            System.out.println(response);
            AbacatePayPixResponseWrapperDto wrapper = response.getBody();
            if (wrapper != null && wrapper.data() != null) {
                return wrapper.data();
            } else {
                throw new RuntimeException("Gateway de pagamento retornou uma resposta vazia ou malformada.");
            }
        } catch (HttpClientErrorException e) {
            throw new RuntimeException("Falha na comunicação com o gateway de pagamento: " + e.getMessage());
        }
    }
}
