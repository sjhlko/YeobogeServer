package com.yeoboge.server.service;

import com.yeoboge.server.domain.dto.PageResponse;
import org.springframework.data.domain.Pageable;

/**
 * 친구 관련 비즈니스 로직에 대한 메서드를 제공하는 인터페이스
 */
public interface FriendService {
    /**
     * 회원의 친구 목록을 페이징하여 조회함.
     *
     * @param id 조회할 회원 ID
     * @param pageable 페이징 관련 정보가 포함된 {@link Pageable}
     * @return 친구 DTO 리스트를 포함한 {@link PageResponse}
     */
    PageResponse getFriends(Long id, Pageable pageable);
}
