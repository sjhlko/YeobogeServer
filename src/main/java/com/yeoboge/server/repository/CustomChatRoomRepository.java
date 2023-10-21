package com.yeoboge.server.repository;

import com.yeoboge.server.domain.dto.boardGame.BoardGameThumbnailDto;
import com.yeoboge.server.domain.dto.chat.ChatRoomResponse;
import com.yeoboge.server.domain.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * 채팅방 관련 {@code QueryDsl} 쿼리를 정의하는 인터페이스
 */
public interface CustomChatRoomRepository {
    /**
     * 특정 회원이 속한 채팅방의 정보가 담긴 {@link ChatRoomResponse} 를 페이징하여 리턴함
     *
     * @param pageable 페이징 정보가 담긴 {@link Pageable}
     * @param currentUser 현재 로그인한 회원
     * @return 페이징된 채팅 방 리스트 {@link ChatRoomResponse} 목록
     */
    Page<ChatRoomResponse> getMyChatRoomList(Pageable pageable, User currentUser);
}
