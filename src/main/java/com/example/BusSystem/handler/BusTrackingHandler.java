package com.example.BusSystem.handler;

import com.example.BusSystem.domain.User.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.security.Principal;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

@Component
public class BusTrackingHandler extends TextWebSocketHandler {

    private static final Logger logger = LoggerFactory.getLogger(BusTrackingHandler.class);

    // Gerencia sessões por ID de ônibus para broadcast de localização
    private final Map<String, List<WebSocketSession>> sessionsByBusId = new ConcurrentHashMap<>();

    // Gerencia sessões por ID de usuário para mensagens diretas (notificações)
    private final Map<String, WebSocketSession> sessionsByUserId = new ConcurrentHashMap<>();

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        String busId = getBusIdFromSession(session);
        Principal principal = session.getPrincipal();

        if (principal == null || !(principal instanceof Authentication) || !((Authentication) principal).isAuthenticated()) {
            logger.warn("Attempted WebSocket connection by unauthenticated or invalid principal.");
            session.close(CloseStatus.POLICY_VIOLATION);
            return;
        }

        Authentication authentication = (Authentication) principal;
        User user = (User) authentication.getPrincipal();
        String userId = user.getId().toString();

        // Registra a sessão para o tracking do ônibus
        sessionsByBusId.computeIfAbsent(busId, k -> new CopyOnWriteArrayList<>()).add(session);
        logger.info("Sessão {} registrada para o ônibus [{}].", session.getId(), busId);

        // Registra a sessão para notificações do usuário
        sessionsByUserId.put(userId, session);
        logger.info("Sessão {} registrada para o usuário [{}].", session.getId(), userId);

        // Armazena os IDs na sessão para facilitar a limpeza
        session.getAttributes().put("busId", busId);
        session.getAttributes().put("userId", userId);

        session.sendMessage(new TextMessage("Conectado ao tracking do ônibus: " + busId));
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        String busId = (String) session.getAttributes().get("busId");
        String userId = (String) session.getAttributes().get("userId");

        // Limpa do mapa de ônibus
        if (busId != null && sessionsByBusId.containsKey(busId)) {
            sessionsByBusId.get(busId).remove(session);
            if (sessionsByBusId.get(busId).isEmpty()) {
                sessionsByBusId.remove(busId);
            }
            logger.info("Sessão {} removida do ônibus [{}].", session.getId(), busId);
        }

        // Limpa do mapa de usuários
        if (userId != null) {
            sessionsByUserId.remove(userId);
            logger.info("Sessão {} removida do usuário [{}].", session.getId(), userId);
        }
    }

    // Este método continua funcionando como antes, para o RASTREAMENTO
    public void sendLocationUpdate(String busId, String locationJson) {
        List<WebSocketSession> sessions = sessionsByBusId.get(busId);
        if (sessions == null) return;

        TextMessage message = new TextMessage(locationJson);
        for (WebSocketSession session : sessions) {
            try {
                if (session.isOpen()) session.sendMessage(message);
            } catch (IOException e) {
                logger.error("Erro ao enviar localização para a sessão {}: {}", session.getId(), e.getMessage());
            }
        }
    }

    public void sendMessageToUser(String userId, String messagePayload) {
        WebSocketSession session = sessionsByUserId.get(userId);
        if (session != null && session.isOpen()) {
            try {
                logger.info("Enviando mensagem para o usuário {}: {}", userId, messagePayload);
                session.sendMessage(new TextMessage(messagePayload));
            } catch (IOException e) {
                logger.error("Falha ao enviar mensagem via WebSocket para o usuário {}", userId, e);
            }
        } else {
            logger.warn("Tentativa de enviar mensagem para o usuário desconectado ou não encontrado: {}", userId);
        }
    }

    private String getBusIdFromSession(WebSocketSession session) {
        if (session.getUri() == null) return null;
        String path = session.getUri().getPath();
        String[] segments = path.split("/");
        return (segments.length > 0) ? segments[segments.length - 1] : null;
    }
}