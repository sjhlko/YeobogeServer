package com.yeoboge.server.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum RecommendTypes {
    TOP_10("top10"),
    PERSONAL_RECOMMEND("personalRec"),
    MY_BOOKMARK("bookmark"),
    FAVORITE_GENRE("favoriteGenre"),
    FRIENDS_FAVORITE("friendsFavorite");

    private String key;
}
