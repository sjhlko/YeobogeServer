package com.yeoboge.server.repository;

import com.yeoboge.server.domain.dto.boardGame.BoardGameDetailDto;
import com.yeoboge.server.domain.dto.boardGame.BoardGameDetailedThumbnailDto;
import com.yeoboge.server.domain.vo.boardgame.SearchBoardGameRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * 보드게임 관련 {@code QueryDsl} 쿼리를 정의하는 인터페이스
 */
public interface CustomBoardGameRepository {
    /**
     * 특정 보드게임의 상세 정보를 조회함.
     *
     * @param boardGameId 조회할 보드게임 ID
     * @return 해당 보드게임의 상세 정보를 포함한 {@link BoardGameDetailDto}
     */
    BoardGameDetailDto getBoardGameDetail(long boardGameId);

    /**
     * 보드게임 검색 조건에 해당하는 보드게임 리스트를 페이징을 적용하여 리턴함
     *
     * @param pageable 페이징 정보가 담긴 {@link Pageable}
     * @param request 검색 조건이 담긴 {@link SearchBoardGameRequest} VO
     * @return 검색 조건에 부합하는 보드게임 {@link BoardGameDetailedThumbnailDto}
     */
    Page<BoardGameDetailedThumbnailDto> findBoardGameBySearchOption(Pageable pageable, SearchBoardGameRequest request);
}
