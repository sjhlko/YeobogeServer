package com.yeoboge.server.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 개인 추천 목록 생성 시 카테고리 별 종류와 각 카테고리의 메타데이터를
 * {@link com.yeoboge.server.domain.dto.recommend.RecommendForSingleResponse}에 추가할 때
 * {@code Map}에 key 값으로 지정할 문자열을 매핑하기 위한 {@code Enum}
 */
@Getter
@AllArgsConstructor
public enum RecommendTypes {
    TOP_10("top10"),
    MY_BOOKMARK("bookmark"),
    FAVORITE_GENRE("favoriteGenre"),
    FRIENDS_FAVORITE("friendsFavorite");

    private String key;
}
