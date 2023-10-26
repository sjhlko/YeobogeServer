package com.yeoboge.server.service;

import com.yeoboge.server.domain.dto.PageResponse;
import com.yeoboge.server.domain.entity.IsRead;
import org.springframework.data.domain.Pageable;

/**
 * 채팅 메세지 관련 비즈니스 로직에 대한 메서드를 제공하는 인터페이스
 */
public interface ChatMessageService {

    /**
     * 특정 채팅 메세지를 저장함
     *
     * @param message 저장될 메세지 내용
     * @param timeStamp 채팅이 전송된 시각
     * @param chatRoomId 채팅이 보내진 채팅 방의 id
     * @param userId 채팅을 보낸 유저의 id
     * @param isRead 해당 채팅이 상대방으로 부터 읽혔는지에 관한 {@link IsRead}
     */
    void saveMessage(String message, String timeStamp, Long chatRoomId, Long userId, IsRead isRead);

    /**
     * 특정 두 유저 간의 채팅 내역을 조회하여 페이징 하여 리턴함
     *
     * @param currentUserId 현재 로그인한 회원의 id
     * @param id 채팅을 한 두 회원 중 현재 로그인한 회원이 아닌 다른 회원의 id
     * @param pageable 페이징 정보가 담긴 {@link Pageable}
     * @return 채팅 내역을 페이징하여 리턴함
     */
    PageResponse getChatMessages(Long currentUserId, Long id, Pageable pageable);

    /**
     * 특정 메세지의 읽음 정보를 수정함
     *
     * @param chatRoomId 특정 채팅이 속한 채팅방의 id
     * @param userId 현재 로그인한 회원의 id
     */
    void changeReadStatus(Long chatRoomId, Long userId);
}
