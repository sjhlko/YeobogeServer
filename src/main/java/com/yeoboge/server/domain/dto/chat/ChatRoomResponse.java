package com.yeoboge.server.domain.dto.chat;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.yeoboge.server.domain.dto.user.UserInfoDto;
import com.yeoboge.server.domain.entity.ChatRoom;
import com.yeoboge.server.domain.entity.User;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.Objects;

@Builder
public record ChatRoomResponse(
        Long id,
        String lastMessage,
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern ="yyyy-MM-dd HH:mm:ss")
        LocalDateTime createdAt,
        Long unReadMessage,
        UserInfoDto userInfo
) {
    public static ChatRoomResponse of(ChatRoom chatRoom, User user){
        User friend = chatRoom.getTargetUser();
        if(Objects.equals(chatRoom.getTargetUser().getId(), user.getId()))
            friend = chatRoom.getCurrentUser();
        UserInfoDto friendInfoDto = UserInfoDto.builder()
                .imagePath(friend.getProfileImagePath())
                .id(friend.getId())
                .nickname(friend.getNickname())
                .build();
        return ChatRoomResponse.builder()
                .id(chatRoom.getId())
                .lastMessage("뭐해") //임시 메시지
                .userInfo(friendInfoDto)
                .build();
    }
}
