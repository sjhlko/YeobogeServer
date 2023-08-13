package com.yeoboge.server.repository.customRepository;

import com.yeoboge.server.domain.entity.BoardGame;
import com.yeoboge.server.domain.vo.boardgame.SearchBoardGameRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * 보드게임 검색 관련 QueryDsl 쿼리 메서드를 정의하는 인터페이스
 */
public interface BoardGameCustomRepository {

    /**
     * 보드게임 검색 조건에 해당하는 보드게임 리스트를 페이징을 적용하여 리턴함
     *
     * @param request 보드게임 검색 조건 {@link SearchBoardGameRequest} Vo
     * @return 검색 조건에 부합하는 보드게임 {@link BoardGame}
     */
    Page<BoardGame> findBoardGameBySearchOption(Pageable pageable, SearchBoardGameRequest request);
}
