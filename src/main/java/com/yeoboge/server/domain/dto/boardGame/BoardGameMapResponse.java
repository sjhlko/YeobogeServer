package com.yeoboge.server.domain.dto.boardGame;

import java.util.List;

/**
 * 보드게임 목록을 조회한 뒤 {@link java.util.Map}에 담아
 * 전달할 때 필요한 메서드가 정의된 인터페이스
 *
 * @param <T> 보드게임 목록 조회 시 각 보드게임에서 필요한 정보를 담을 DTO
 */
public interface BoardGameMapResponse<T> {
    /**
     * {@code key}에 해당하는 리스트를 맵에 추가함.
     *
     * @param boardGames 맵에 추가할 {@code <T>} 타입의 리스트
     * @param key 맵의 키
     */
    void addBoardGames(List<T> boardGames, Object key);
}
