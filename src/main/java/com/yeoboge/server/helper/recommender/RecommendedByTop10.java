package com.yeoboge.server.helper.recommender;

import com.yeoboge.server.domain.dto.boardGame.BoardGameThumbnailDto;
import com.yeoboge.server.domain.dto.recommend.RecommendForSingleResponse;
import com.yeoboge.server.enums.RecommendTypes;
import com.yeoboge.server.repository.RecommendRepository;
import lombok.Builder;

import java.util.List;
import java.util.concurrent.CountDownLatch;

public class RecommendedByTop10 extends RecommendedBySomethingBase implements RecommendedBySomething {
    @Builder
    public RecommendedByTop10(RecommendRepository repository, RecommendTypes type) {
        super(repository, type);
        this.description = "현재 인기 보드게임 🌟";
    }

    @Override
    public void addRecommendedDataToResponse(RecommendForSingleResponse response, CountDownLatch latch) {
        List<BoardGameThumbnailDto> boardGames = repository.getTopTenBoardGames();
        addToResponse(response, boardGames, latch);
    }
}
