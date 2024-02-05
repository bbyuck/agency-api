package com.ndm.core.config;

import com.ndm.core.model.Current;
import com.ndm.core.model.WebSocketMemberSession;
import lombok.RequiredArgsConstructor;
import org.json.simple.parser.JSONParser;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

import java.util.Map;

@Configuration
@EnableWebSocket
@RequiredArgsConstructor
public class WebSocketConfig implements WebSocketConfigurer {


    @Value("${client.location}")
    private String clientLocation;
    private final JSONParser jsonParser;
    private final WebSocketMemberSession webSocketMemberSession;

    @Bean
    public WebSocketHandler webSocketHandler() {
        return new com.ndm.core.common.util.WebSocketHandler(webSocketMemberSession, jsonParser);
    }

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(webSocketHandler(), "/ws").setAllowedOrigins(clientLocation);
    }

}
