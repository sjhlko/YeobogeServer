package com.yeoboge.server.domain.dto.chat;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.yeoboge.server.domain.entity.ChatMessage;
import com.yeoboge.server.domain.entity.User;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.Objects;

@Builder
public record ChatMessageResponse(
        Long id,
        String message,
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern ="yyyy-MM-dd HH:mm:ss")
        LocalDateTime createdAt,
        Boolean isMyMessage

) {

    public static ChatMessageResponse of(ChatMessage chatMessage, User currentUser){
        User sender = chatMessage.getUser();
        return ChatMessageResponse.builder()
                .id(chatMessage.getId())
                .message(chatMessage.getMessage())
                .createdAt(chatMessage.getCreatedAt())
                .isMyMessage(Objects.equals(currentUser.getId(), sender.getId()) ? Boolean.TRUE : Boolean.FALSE)
                .build();
    }

}
