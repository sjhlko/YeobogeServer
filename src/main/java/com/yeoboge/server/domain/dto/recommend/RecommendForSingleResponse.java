package com.yeoboge.server.domain.dto.recommend;

import com.yeoboge.server.domain.dto.boardGame.BoardGameThumbnailDto;
import com.yeoboge.server.helper.recommender.RecommendedBySomething;

import java.util.List;
import java.util.Map;

/**
 * 개인 사용자에게 추천 및 보드게임 목록을 전달할 VO
 *
 * @param shelves 각 카테고리 별로 {@link BoardGameThumbnailDto} 목록이 연결된 {@link Map}
 */
public record RecommendForSingleResponse(
        List<String> keys,
        Map<String, List<BoardGameThumbnailDto>> shelves,
        Map<String, String> descriptions
) {
    public void setMetadata(List<RecommendedBySomething> metadataList) {
        for (RecommendedBySomething metadata : metadataList) {
            keys.add(metadata.getKey());
            descriptions.put(metadata.getKey(), metadata.getDescription());
        }
    }
}
