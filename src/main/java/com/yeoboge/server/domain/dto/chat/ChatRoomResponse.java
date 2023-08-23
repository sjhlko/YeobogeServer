package com.yeoboge.server.domain.dto.chat;

import com.yeoboge.server.domain.dto.friend.FriendInfoDto;
import com.yeoboge.server.domain.entity.ChatRoom;
import com.yeoboge.server.domain.entity.User;
import lombok.Builder;

import java.util.Objects;

@Builder
public record ChatRoomResponse(
        Long id,
        String lastMessage,
        FriendInfoDto userInfo
) {
    public static ChatRoomResponse of(ChatRoom chatRoom, User user){
        User friend = chatRoom.getTargetUser();
        if(Objects.equals(chatRoom.getTargetUser().getId(), user.getId()))
            friend = chatRoom.getCurrentUser();
        FriendInfoDto friendInfoDto = FriendInfoDto.builder()
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
