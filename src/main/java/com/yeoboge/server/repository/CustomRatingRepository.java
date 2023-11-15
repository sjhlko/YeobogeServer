package com.yeoboge.server.repository;

import com.yeoboge.server.domain.dto.boardGame.BoardGameThumbnailDto;
import com.yeoboge.server.domain.dto.boardGame.FriendReviewDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

/**
 * 평가한 보드게임 관련 QueryDsl 쿼리 메서드를 정의하는 인터페이스
 */
public interface CustomRatingRepository {
    /**
     * 회원이 평가한 보드게임의 별점 그룹만 조회함.
     *
     * @param userId 조회를 요청한 회원 ID
     * @return 회원이 평가를 남긴 별점 리스트
     */
    List<Double> getUserRatingGroup(final Long userId);

    /**
     * 회원이 평가한 보드게임 중 특정 별점의 목록에 대해 최신 순으로 일정 개수 조회함.
     *
     * @param userId 조회를 요청한 회원 ID
     * @param rate 조회할 별점
     * @return 해당 별점의 보드게임 목록
     */
    List<BoardGameThumbnailDto> getRatingByUserId(final Long userId, final Double rate);

    /**
     * 회원이 평가한 보드게임 중 특정 별점의 목록을 페이징하여 조회함.
     *
     * @param userId 조회를 요청한 회원 ID
     * @param score 조회할 별점
     * @return 페이징된 해당 별점의 보드게임 {@link BoardGameThumbnailDto} 목록
     */
    Page<BoardGameThumbnailDto> getRatingsByUserId(final Long userId, final Double score, final Pageable pageable);

    /**
     * 특정 보드게임에 대한 사용자의 친구들이 남긴 평가를 조회함.
     *
     * @param userId 사용자 ID
     * @param boardGameId 보드게임 ID
     * @param isForLike 이 보드게임을 좋아하는 친구를 찾을 것 인지 좋아하지 않는 친구를 찾을 것인지에 대한 {@code boolean}
     * @return
     */
    FriendReviewDto getFriendReviewOfBoardGame(final long userId, final long boardGameId, boolean isForLike);

    /**
     * 현재 날짜로부터 어제까지 사용자가 평가한 보드게임의 전체 개수를 조회함.
     *
     * @param userId 조회한 사용자 ID
     * @return 해당 사용자가 평가한 전체 보드게임 수
     */
    long countByUserUntilYesterday(final long userId);
}
