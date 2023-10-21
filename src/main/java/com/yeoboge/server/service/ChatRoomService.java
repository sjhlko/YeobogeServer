package com.yeoboge.server.service;

import com.yeoboge.server.domain.dto.PageResponse;
import com.yeoboge.server.domain.entity.IsRead;
import org.springframework.data.domain.Pageable;

/**
 * 채팅방 관련 비즈니스 로직에 대한 메서드를 제공하는 인터페이스
 */
public interface ChatRoomService {

    /**
     * 두 유저를 통해 해당 두 유저의 채팅이 저장되는 채팅 방을 검색하여 해당 채팅방의 id를 리턴함
     *
     * @param currentUserId 현재 로그인한 회원의 id
     * @param targetUserId 채팅 상대방 회원의 id
     * @return 두 회원의 채팅이 저장되는 채팅 방의 id
     */
    Long findChatRoomIdByUsers(Long currentUserId, Long targetUserId);

    /**
     * 특정 유저의 채팅방 목록을 페이징하여 리턴함
     *
     * @param id 현재 로그인한 회원의 id
     * @param pageable 페이징 정보가 담긴 {@link Pageable}
     * @return 특정 회원의 채팅방 목록을 페이징하여 리턴함
     */
    PageResponse getChatRooms (Long id, Pageable pageable);
}
