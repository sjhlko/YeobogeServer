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
) implements RecommendationResponse {
    /**
     * 추천 보드게임 목록 카테고리에 따라 해당하는 key, 타이틀, 추천 목록을 {@code Map}에 추가함.
     *
     * @param boardGames 추천 보드게임 {@link BoardGameThumbnailDto} 리스트
     * @param key 추천 보드게임 목록 카테고리 key
     * @param description 추천 보드게임 목록 카테고리 타이틀
     */
    @Override
    public void addRecommendationsForIndividual(
            List<BoardGameThumbnailDto> boardGames, String key, String description
    ) {
        keys.add(key);
        shelves.put(key, boardGames);
        descriptions.put(key, description);
    }
}
