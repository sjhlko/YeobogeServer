package com.yeoboge.server.handler;

import com.yeoboge.server.config.security.JwtProvider;
import com.yeoboge.server.enums.error.AuthenticationErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Order(Ordered.HIGHEST_PRECEDENCE + 99)
public class WebSocketPreHandler implements ChannelInterceptor {
    private static final int TOKEN_SPLIT_INDEX = 7;
    private final JwtProvider jwtProvider;

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(message);
        String authorizationHeader = String.valueOf(headerAccessor.getNativeHeader("Authorization"));
        if (authorizationHeader == null || authorizationHeader.equals("null"))
            throw new AppException(AuthenticationErrorCode.TOKEN_INVALID);

        String accessToken = authorizationHeader.substring(TOKEN_SPLIT_INDEX);
        headerAccessor.addNativeHeader("token", accessToken);
        long userId = jwtProvider.parseUserId(accessToken);
        if (!jwtProvider.isValid(accessToken, userId))
            throw new AppException(AuthenticationErrorCode.TOKEN_INVALID);
        return message;
    }
}