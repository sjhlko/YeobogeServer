package com.yeoboge.server.helper.recommender;

import com.yeoboge.server.domain.dto.boardGame.BoardGameThumbnailDto;
import com.yeoboge.server.domain.dto.recommend.RecommendForSingleResponse;
import com.yeoboge.server.enums.RecommendTypes;
import com.yeoboge.server.repository.RecommendRepository;
import lombok.Builder;

import java.util.List;
import java.util.concurrent.CountDownLatch;

public class RecommendedByBookmark extends RecommendedBySomethingBase implements RecommendedBySomething {
    private long userId;

    @Builder
    public RecommendedByBookmark(
            RecommendRepository repository, RecommendTypes type, long userId, String userNickname
    ) {
        super(repository, type);
        this.userId = userId;
        this.description = userNickname + "님이 찜한 보드게임 🔖";
    }

    @Override
    public void addRecommendedDataToResponse(RecommendForSingleResponse response, CountDownLatch latch) {
        List<BoardGameThumbnailDto> boardGames = repository.getMyBookmarkedBoardGames(userId);
        addToResponse(response, boardGames, latch);
    }
}
