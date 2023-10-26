package com.yeoboge.server.domain.dto.chat;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.yeoboge.server.domain.entity.ChatMessage;
import com.yeoboge.server.domain.entity.User;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * 채팅 메세지의 정보를 담고 있는 DTO
 *
 * @param id 채팅 메세지 ID
 * @param message 메세지 내용
 * @param createdAt 메세지를 보낸 시각
 * @param isMyMessage 현재 로그인한 회원이 보낸 메세지인지에 대한 boolean 변수
 */
@Builder
public record ChatMessageResponse(
        Long id,
        String message,
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern ="yyyy-MM-dd HH:mm:ss")
        LocalDateTime createdAt,
        Boolean isMyMessage

) {

    /**
     * {@link ChatMessage}, {@link User} 엔티티로부터 {@link ChatMessage} DTO를 생성함.
     *
     * @param chatMessage 특정 채팅 메세지
     * @param currentUser 현재 로그인한 회원
     * @return 해당 채팅 메세지에 대한 정보가 담긴 {@link ChatMessageResponse}
     */
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
