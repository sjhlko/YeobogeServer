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
}
