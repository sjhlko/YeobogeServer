package com.yeoboge.server.config;

import com.yeoboge.server.handler.WebSocketErrorHandler;
import com.yeoboge.server.handler.WebSocketPreHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
@RequiredArgsConstructor
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {
    private final WebSocketPreHandler webSocketPreHandler;
    private final WebSocketErrorHandler webSocketErrorHandler;

    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        registration.interceptors(webSocketPreHandler);
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        //클라이언트의 send요청 처리
        registry.setApplicationDestinationPrefixes("/pub");
        //sub하는 클라이언트에게 메시지 전달
        registry.enableSimpleBroker("/sub");
    }


    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("chats/send-message/{userId}").setAllowedOriginPatterns("*");
        registry.setErrorHandler(webSocketErrorHandler);
    }

}