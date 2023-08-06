package com.yeoboge.server.domain.dto.boardGame;

import com.yeoboge.server.domain.entity.BoardGame;
import java.util.List;

/**
 * 보드게임 목록을 조회할 때 필요한 메서드가 정의된 인터페이스
 *
 * @param <T> 보드게임 목록 조회 시 각 보드게임에서 필요한 정보를 담을 DTO
 */
public interface BoardGameListResponse<T> {
    /**
     * {@link BoardGame} 리스트에서 필요한 데이터만 {@code <T>} 형식으로 파싱한 뒤
     * {@code <T>} 리스트에 추가함.
     *
     * @param boardGames 원본 보드게임 리스트
     */
    void addBoardGames(List<BoardGame> boardGames);
}
