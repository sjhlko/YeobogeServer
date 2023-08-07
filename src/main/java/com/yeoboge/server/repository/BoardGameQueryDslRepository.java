package com.yeoboge.server.repository;

import com.yeoboge.server.domain.entity.BoardGame;
import com.yeoboge.server.enums.BoardGameOrderColumn;

import java.util.List;

/**
 * 보드게임 관련 {@code QueryDsl} 쿼리를 정의하는 인터페이스
 */
public interface BoardGameQueryDslRepository {
    /**
     * 회원이 찜한 보드게임 목록을 일정 개수만큼 조회함.
     *
     * @param userId 조회를 요청한 회원 ID
     * @param page 조회할 목록의 페이지 번호
     * @param order 보드게임 목록의 정렬 기준 {@link BoardGameOrderColumn}
     * @return {@link BoardGame} 리스트
     */
    List<BoardGame> getBookmarkByUserId(final Long userId, final Integer page, final BoardGameOrderColumn order);

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
    List<BoardGame> getRatingByUserId(final Long userId, final Double rate);
}
