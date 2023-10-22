package com.yeoboge.server.repository;

import com.yeoboge.server.domain.dto.chat.ChatRoomResponse;
import com.yeoboge.server.domain.entity.User;

import java.util.List;

/**
 * 채팅방 관련 {@code QueryDsl} 쿼리를 정의하는 인터페이스
 */
public interface CustomChatRoomRepository {
    /**
     * 특정 회원이 속한 채팅방의 정보가 담긴 {@link ChatRoomResponse} 리스트를 리턴함
     *
     * @param currentUser 현재 로그인한 회원
     * @return 채팅 방 정보 리스트 {@link ChatRoomResponse}
     */
    List<ChatRoomResponse> getMyChatRoomList(User currentUser);
}
