package com.yeoboge.server.repository;

import com.yeoboge.server.domain.dto.user.UserInfoDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CustomFriendRequestRepository {
    /**
     * 친구 요청 목록을 페이징을 적용해 조회함.
     *
     * @param id 조회할 회원 ID
     * @param pageable 페이징 정보가 담긴 {@link Pageable}
     * @return 페이징된 친구 요청한 사용자의 {@link UserInfoDto} 목록
     */
    Page<UserInfoDto> getFriendRequestsPage(final Long id, final Pageable pageable);
}
