package com.example.BusSystem.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**") // 1. Aplica a configuração a todos os endpoints sob /api/
                .allowedOrigins("http://localhost:63342", "http://127.0.0.1:5500") // 2. Lista as origens permitidas (seu frontend)
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS") // 3. Lista os métodos HTTP permitidos
                .allowedHeaders("*") // 4. Permite todos os headers na requisição
                .allowCredentials(true); // 5. Permite o envio de credenciais (como cookies ou tokens de autorização)
    }
}
