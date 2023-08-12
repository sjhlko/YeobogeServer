package com.yeoboge.server.domain.dto.boardGame;

import lombok.Getter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 보드게임 관련 데이터들을 {@link Map}에 담아 각 특성에 맞게
 * 분류해서 저장한 뒤 넘겨주는 보드게임 목록
 *
 * @param <T> {@code Map}에 담을 리스트의 클래스 타입
 */
/* 추후에 개인 추천 목록 전달 시 동일한 방식으로 사용하기 위해 추상 클래스로 분리 */
@Getter
public abstract class AbstractBoardGameMapResponse<T> implements BoardGameMapResponse<T> {
    protected final Map<Object, List<T>> boardGames = new HashMap<>();

    /**
     * {@code key}에 해당하는 리스트를 맵에 추가함.
     *
     * @param boardGames 맵에 추가할 {@code <T>} 타입의 리스트
     * @param key 맵의 키
     */
    public abstract void addBoardGames(List<T> boardGames, Object key);
}
