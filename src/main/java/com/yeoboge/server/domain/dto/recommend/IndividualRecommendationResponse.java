package com.yeoboge.server.domain.dto.recommend;

import com.yeoboge.server.domain.dto.boardGame.BoardGameThumbnailDto;

import java.util.List;
import java.util.Map;
import java.util.Queue;

/**
 * 개인 사용자에게 추천 및 보드게임 목록을 전달할 DTO
 *
 * @param shelves 각 카테고리 별로 {@link BoardGameThumbnailDto} 목록이 연결된 {@link Map}
 */
public record IndividualRecommendationResponse(
        Queue<String> keys,
        Map<String, List<BoardGameThumbnailDto>> shelves,
        Map<String, String> descriptions
) {
    /**
     * 사용자에게 추천할 보드게임 카테고리의 타입에 따른 key 값을 추가함.
     *
     * @param key 추천 보드게임 목록 카테고리 key
     */
    public void addKey(String key) {
        this.keys.add(key);
    }

    /**
     * 추천 보드게임 목록을 해당 카테고리 key 값에 매핑되도록 {@code Map}에 추가함.
     *
     * @param key 추천 보드게임 목록 카테고리 key
     * @param thumbnails 추천 보드게임 {@link BoardGameThumbnailDto} 리스트
     */
    public void addBoardGameThumbnails(String key, List<BoardGameThumbnailDto> thumbnails) {
        this.shelves.put(key, thumbnails);
    }

    /**
     * 추천 보드게임 목록 카테고리에 맞는 타이틀을 key 값에 매핑되도록 {@code Map}에 추가함.
     *
     * @param key 추천 보드게임 목록 카테고리 key
     * @param description 추천 보드게임 목록 카테고리 타이틀
     */
    public void addDescription(String key, String description) {
        this.descriptions.put(key, description);
    }
}
