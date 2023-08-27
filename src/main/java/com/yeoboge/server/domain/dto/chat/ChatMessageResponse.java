package com.yeoboge.server.domain.dto.chat;

import com.yeoboge.server.domain.dto.friend.FriendInfoDto;
import com.yeoboge.server.domain.entity.ChatMessage;
import com.yeoboge.server.domain.entity.User;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.Objects;

@Builder
public record ChatMessageResponse(
        Long id,
        FriendInfoDto userInfo,
        String message,
        LocalDateTime createdAt,
        Boolean isMyMessage

) {

    public static ChatMessageResponse of(ChatMessage chatMessage, User currentUser){
        User sender = chatMessage.getUser();
        FriendInfoDto friendInfoDto = FriendInfoDto.builder()
                .imagePath(sender.getProfileImagePath())
                .id(sender.getId())
                .nickname(sender.getNickname())
                .build();
        return ChatMessageResponse.builder()
                .id(chatMessage.getId())
                .userInfo(friendInfoDto)
                .message(chatMessage.getMessage())
                .createdAt(chatMessage.getCreatedAt())
                .isMyMessage(Objects.equals(currentUser.getId(), sender.getId()) ? Boolean.TRUE : Boolean.FALSE)
                .build();
    }

}
