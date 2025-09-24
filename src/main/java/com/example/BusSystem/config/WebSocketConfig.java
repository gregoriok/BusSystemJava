package com.example.BusSystem.config;

import com.example.BusSystem.handler.BusTrackingHandler;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;
import org.springframework.web.socket.server.support.HttpSessionHandshakeInterceptor;

@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {

    private final BusTrackingHandler busTrackingHandler;

    public WebSocketConfig(BusTrackingHandler busTrackingHandler) {
        this.busTrackingHandler = busTrackingHandler;
    }

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        // Registra o handler para o endpoint /ws/bus/{id}
        registry.addHandler(busTrackingHandler, "/ws/bus/{busId}")
                .addInterceptors(new HttpSessionHandshakeInterceptor())
                .setAllowedOrigins("*");
    }
}