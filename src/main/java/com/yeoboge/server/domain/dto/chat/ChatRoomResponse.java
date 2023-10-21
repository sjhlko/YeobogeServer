package com.yeoboge.server.domain.dto.chat;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.yeoboge.server.domain.dto.user.UserInfoDto;
import com.yeoboge.server.domain.entity.ChatMessage;
import com.yeoboge.server.domain.entity.ChatRoom;
import com.yeoboge.server.domain.entity.User;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.Objects;
/**
 * 채팅방의 정보를 담고 있는 DTO
 *
 * @param id 채팅 방 ID
 * @param lastMessage 가장 최근에 보낸 메세지의 내용
 * @param createdAt 가장 최근에 보낸 메세지를 보낸 시각
 * @param unReadMessage 현재 로그인한 회원이 해당 채팅방에 보내진 메세지 중 읽지 않은 메세지의 수
 * @param userInfo 해당 채팅방에 대한 상대 유저에 대한 정보
 */

@Builder
public record ChatRoomResponse(
        Long id,
        String lastMessage,
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern ="yyyy-MM-dd HH:mm:ss")
        LocalDateTime createdAt,
        Long unReadMessage,
        UserInfoDto userInfo
) { }
