package com.yeoboge.server.service;

import com.yeoboge.server.domain.dto.boardGame.BoardGameListResponse;
import com.yeoboge.server.domain.dto.user.UserDetailResponse;
import com.yeoboge.server.domain.dto.user.UserUpdateRequest;
import com.yeoboge.server.domain.vo.response.MessageResponse;
import com.yeoboge.server.enums.BoardGameOrderColumn;
import org.springframework.web.multipart.MultipartFile;

/**
 * 회원 관련 비즈니스 로직에 대한 메서드를 제공하는 인터페이스
 */
public interface UserService {
    /**
     * 특정 회원의 정보를 반환하는 메서드
     *
     * @param id 정보를 반환할 회원의 인덱스
     * @return {@link UserDetailResponse}
     */
    UserDetailResponse getProfile(Long id);

    /**
     * 특정 회원의 정보를 수정하는 메서드
     *
     * @param file 변경할 프로필 사진 파일
     * @param request 프로필 사진 변경 여부와 변경하고자 하는 닉네임이 담긴 {@link UserUpdateRequest} VO
     * @param id 정보를 수정할 회원의 인덱스
     * @return {@link UserDetailResponse}
     */
    MessageResponse updateUser(MultipartFile file, UserUpdateRequest request, Long id);

    /**
     * 회원이 찜한 보드게임 목록을 조회함.
     *
     * @param id 조회를 요청한 회원 ID
     * @param page 조회할 보드게임 목록의 페이지 번호
     * @param order 목록의 정렬 기준 {@link BoardGameOrderColumn}
     * @return {@link com.yeoboge.server.domain.dto.user.BookmarkListResponse}
     */
    BoardGameListResponse getMyBookmarks(Long id, Integer page, BoardGameOrderColumn order);
}
