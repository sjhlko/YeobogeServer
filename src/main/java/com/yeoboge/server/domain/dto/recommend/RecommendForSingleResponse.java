package com.yeoboge.server.domain.dto.recommend;

import com.yeoboge.server.domain.dto.boardGame.BoardGameThumbnailDto;
import com.yeoboge.server.enums.RecommendTypes;

import java.util.Arrays;
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
    public void setMetadata(String nickname, String genre) {
        for (RecommendTypes type : Arrays.asList(RecommendTypes.values())) keys.add(type.getKey());

        descriptions.put(RecommendTypes.MY_BOOKMARK.getKey(), nickname + "님이 찜한 보드게임");
        descriptions.put(RecommendTypes.TOP_10.getKey(), "올해 인기 보드게임 TOP 10");
        descriptions.put(RecommendTypes.FAVORITE_GENRE.getKey(), nickname + "님이 좋아하는 " + genre + " 보드게임");
        descriptions.put(RecommendTypes.FRIENDS_FAVORITE.getKey(), nickname + "님의 친구들이 좋아하는 보드게임");
        descriptions.put(RecommendTypes.PERSONAL_RECOMMEND.getKey(), nickname + "님만을 위한 추천 보드게임");
    }
}
