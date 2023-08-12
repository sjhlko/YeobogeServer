package com.yeoboge.server.service;

import com.yeoboge.server.domain.dto.PageResponse;
import com.yeoboge.server.domain.dto.boardGame.BoardGameMapResponse;
import com.yeoboge.server.domain.dto.boardGame.TotalRatingsResponse;
import com.yeoboge.server.domain.dto.user.UserDetailResponse;
import com.yeoboge.server.domain.dto.user.UserUpdateRequest;
import com.yeoboge.server.domain.vo.PageRequest;
import com.yeoboge.server.domain.vo.response.MessageResponse;
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
     * @param id    조회를 요청한 회원 ID
     * @param pageRequest  조회할 보드게임 목록의 페이징 정보 {@link PageRequest}
     * @return {@link PageResponse}
     */
    PageResponse getMyBookmarks(Long id, PageRequest pageRequest);

    /**
     * 회원이 평가한 보드게임을 각 별점 별로 조회함.
     *
     * @param id 조회를 요청한 회원 ID
     * @return {@link TotalRatingsResponse}
     */
    BoardGameMapResponse getMyAllRatings(Long id);

    /**
     * 회원이 평가한 보드게임 중 특정 별점의 목록을 조회함.
     *
     * @param id    조회를 요청한 회원 ID
     * @param score 조회할 별점
     * @param pageRequest  조회할 보드게임 목록의 페이징 정보 {@link PageRequest}
     * @return {@link PageResponse}
     */
    PageResponse getMyRatingsByScore(Long id, Double score, PageRequest pageRequest);
}
