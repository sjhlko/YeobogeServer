package com.yeoboge.server.helper.recommender;

import com.yeoboge.server.domain.dto.boardGame.BoardGameDetailedThumbnailDto;
import com.yeoboge.server.domain.dto.recommend.GroupRecommendationResponse;
import com.yeoboge.server.enums.error.GroupErrorCode;
import com.yeoboge.server.handler.AppException;
import com.yeoboge.server.repository.RecommendRepository;
import lombok.Builder;

import java.util.List;

@Builder
public class HistoryGroupRecommender implements GroupRecommender {
    private long id, userId;
    private RecommendRepository repository;

    @Override
    public void addRecommendationsToResponse(GroupRecommendationResponse response) {
        List<BoardGameDetailedThumbnailDto> history =
                repository.getRecommendationHistoriesWithDetail(userId, id);
        checkIsHistoryExists(history);
        response.addRecommendations(history);
    }

    private void checkIsHistoryExists(List<BoardGameDetailedThumbnailDto> history) {
        if (history.isEmpty())
            throw new AppException(GroupErrorCode.RECOMMENDATION_HISTORY_NOT_FOUND);
    }
}
