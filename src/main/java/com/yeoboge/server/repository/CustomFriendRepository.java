package com.yeoboge.server.repository;

import com.yeoboge.server.domain.dto.user.UserInfoDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * 친구 관련 {@code QueryDsl} 쿼리를 정의하는 인터페이스
 */
public interface CustomFriendRepository {
    /**
     * 친구 목록을 페이징을 적용해 조회함.
     *
     * @param id 조회할 회원 ID
     * @param pageable 페이징 정보가 담긴 {@link Pageable}
     * @return 페이징된 친구 {@link UserInfoDto} 목록
     */
    Page<UserInfoDto> getFriendsPage(final Long id, final Pageable pageable);
}
