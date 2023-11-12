package com.yeoboge.server.handler;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.yeoboge.server.config.security.JwtProvider;
import com.yeoboge.server.enums.error.AuthenticationErrorCode;
import com.yeoboge.server.service.ChatMessageService;
import com.yeoboge.server.service.ChatRoomService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageDeliveryException;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Component
@RequiredArgsConstructor
@Order(Ordered.HIGHEST_PRECEDENCE + 99)
public class WebSocketPreHandler implements ChannelInterceptor {
    private static final int TOKEN_SPLIT_INDEX = 7;
    private static final String SUB_DESTINATION_SPLIT = "/sub/send-message/";
    private static final String PUB_DESTINATION_SPLIT = "/pub/send-message/";
    private final ChatRoomService chatRoomService;
    private final ChatMessageService chatMessageService;
    private final JwtProvider jwtProvider;
    private static Map<String, Long> openedSession = new ConcurrentHashMap<>();
    private static Map<String, Long> connectedRoom = new ConcurrentHashMap<>();
    private static Map<Long, Set<String>> connectedRoomSize = new ConcurrentHashMap<>();

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor headerAccessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
        if (Objects.equals(headerAccessor.getCommand(), StompCommand.CONNECT)) {
            String authorizationHeader = String.valueOf(headerAccessor.getNativeHeader("Authorization"));
            if (authorizationHeader == null || !authorizationHeader.contains("Bearer"))
                throw new MessageDeliveryException(AuthenticationErrorCode.TOKEN_INVALID.getMessage());

            authorizationHeader = authorizationHeader.substring(1, authorizationHeader.length() - 1);
            String accessToken = authorizationHeader.substring(TOKEN_SPLIT_INDEX);
            long userId = validateToken(accessToken);
            headerAccessor.addNativeHeader("id", String.valueOf(userId));
        }
        if (Objects.equals(headerAccessor.getCommand(), StompCommand.SEND)) {
            String content = new String((byte[]) message.getPayload());
            JsonObject msg = (JsonObject) JsonParser.parseString(content);
            String token = msg.get("accessToken").toString();
            long userId = validateToken(token.substring(1, token.length() - 1));
            headerAccessor.addNativeHeader("id", String.valueOf(userId));
            Long roomId = Long.parseLong(headerAccessor.getDestination().split(PUB_DESTINATION_SPLIT)[1]);
            if (connectedRoomSize.get(roomId).size() < 2)
                headerAccessor.addNativeHeader("opponentConnected", "false");
            else headerAccessor.addNativeHeader("opponentConnected", "true");
            //headerAccessor.addNativeHeader("roomId", connectedRoom.get(headerAccessor.getSessionId()).toString());
        }
        if (Objects.equals(headerAccessor.getCommand(), StompCommand.SUBSCRIBE)) {
            Long userId = openedSession.get(headerAccessor.getSessionId());
            Long roomId = Long.parseLong(headerAccessor.getDestination().split(SUB_DESTINATION_SPLIT)[1]);
//            connectedRoom.put(headerAccessor.getSessionId(), roomId);
            connectedRoomSize.putIfAbsent(roomId, new HashSet<>());
            connectedRoomSize.get(roomId).add(headerAccessor.getSessionId());
            chatMessageService.changeReadStatus(roomId, userId);
            //return MessageBuilder.createMessage(new byte[0], headerAccessor.getMessageHeaders());
        }
        return message;
    }

    @EventListener(SessionConnectEvent.class)
    public void onConnect(SessionConnectEvent event) {
        String sessionId = event.getMessage().getHeaders().get("simpSessionId").toString();
        String userId = event.getMessage().getHeaders().get("nativeHeaders")
                .toString().split("id=\\[")[1].split("]")[0];
        openedSession.put(sessionId, Long.parseLong(userId));
    }

    @EventListener(SessionDisconnectEvent.class)
    public void onDisconnect(SessionDisconnectEvent event) {
        openedSession.remove(event.getSessionId());
        connectedRoomSize.get(connectedRoom.get(event.getSessionId())).remove(event.getSessionId());
//        connectedRoom.remove(event.getSessionId());
    }

    private Long validateToken(String token) {
        long userId = jwtProvider.parseUserId(token);
        if (!jwtProvider.isValid(token, userId))
            throw new MessageDeliveryException(AuthenticationErrorCode.TOKEN_INVALID.getMessage());
        return userId;
    }

    private String modifyDestination(Long roomId) {
        return SUB_DESTINATION_SPLIT + roomId;
    }
}
