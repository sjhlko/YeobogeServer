package com.yeoboge.server.handler;

import com.yeoboge.server.config.security.JwtProvider;
import com.yeoboge.server.enums.error.AuthenticationErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageDeliveryException;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.Objects;

@Component
@RequiredArgsConstructor
@Order(Ordered.HIGHEST_PRECEDENCE + 99)
public class WebSocketPreHandler implements ChannelInterceptor {
    private static final int TOKEN_SPLIT_INDEX = 7;
    private final JwtProvider jwtProvider;

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor headerAccessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
        if (Objects.equals(headerAccessor.getCommand(), StompCommand.CONNECT)
//                Objects.equals(headerAccessor.getCommand(), StompCommand.SEND)
                ) {
            String authorizationHeader = String.valueOf(headerAccessor.getNativeHeader("Authorization"));
            if (authorizationHeader == null || !authorizationHeader.contains("Bearer"))
                throw new MessageDeliveryException(AuthenticationErrorCode.TOKEN_INVALID.getMessage());

            authorizationHeader = authorizationHeader.substring(1, authorizationHeader.length() - 1);
            String accessToken = authorizationHeader.substring(TOKEN_SPLIT_INDEX);
            long userId = jwtProvider.parseUserId(accessToken);
            if (!jwtProvider.isValid(accessToken, userId))
                throw new MessageDeliveryException(AuthenticationErrorCode.TOKEN_INVALID.getMessage());
            headerAccessor.addNativeHeader("id", String.valueOf(userId));
        }
        if (Objects.equals(headerAccessor.getCommand(), StompCommand.SUBSCRIBE))
            return MessageBuilder.createMessage(new byte[0], headerAccessor.getMessageHeaders());
        return message;
    }
}