package com.synclink.infrastructure.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        // Prefixo para mensagens que são roteadas para métodos anotados com @MessageMapping
        config.setApplicationDestinationPrefixes("/app");

        // Configuração do broker simples para tópicos e filas
        config.enableSimpleBroker("/topic", "/queue", "/user");

        // Prefixo para mensagens direcionadas a usuários específicos
        config.setUserDestinationPrefix("/user");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // Endpoint principal para conexão WebSocket
        registry.addEndpoint("/ws")
                .setAllowedOriginPatterns("*")
                .withSockJS(); // Suporte para fallback com SockJS

        // Endpoint alternativo sem SockJS para clientes que suportam WebSocket nativo
        registry.addEndpoint("/ws")
                .setAllowedOriginPatterns("*");

        // Endpoint para teste
        registry.addEndpoint("/ws-test")
                .setAllowedOriginPatterns("*")
                .withSockJS();


    }
}