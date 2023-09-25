package com.yeoboge.server.helper.recommender;

import com.yeoboge.server.domain.dto.boardGame.BoardGameThumbnailDto;
import com.yeoboge.server.domain.dto.recommend.RecommendForSingleResponse;
import com.yeoboge.server.enums.RecommendTypes;
import com.yeoboge.server.repository.RecommendRepository;

import java.util.List;
import java.util.concurrent.CountDownLatch;

/**
 * 개인 추천 목록 생성 시 각 카테고리 별로 공통적으로
 * 사용되는 로직을 분리하기 위한 추상 클래스
 */
public abstract class RecommendedBySomethingBase implements RecommendedBySomething {
    protected RecommendRepository repository;
    protected String key;
    protected String description;

    RecommendedBySomethingBase(RecommendRepository repository, RecommendTypes type) {
        this.repository = repository;
        this.key = type.getKey();
    }

    /**
     * 추천 보드게임 목록이 생성된 카테고리에 대해서만
     * 해당 카테고리의 key, {@link BoardGameThumbnailDto} 리스트, 타이틀을
     * {@link RecommendForSingleResponse}에 추가함.
     *
     * @param response {@link RecommendForSingleResponse}
     * @param boardGames 사용자에게 추천할 보드게임 {@link BoardGameThumbnailDto} 리스트
     */
    protected void addToResponse(RecommendForSingleResponse response, List<BoardGameThumbnailDto> boardGames) {
        if (boardGames.isEmpty()) return;

        response.addKey(key);
        response.addBoardGameThumbnails(key, boardGames);
        response.addDescription(key, description);
    }

    public abstract void addRecommendedDataToResponse(RecommendForSingleResponse response, CountDownLatch latch);
}
