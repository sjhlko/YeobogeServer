package com.yeoboge.server.helper.recommender;

import com.yeoboge.server.domain.dto.boardGame.BoardGameThumbnailDto;
import com.yeoboge.server.enums.RecommendTypes;
import com.yeoboge.server.repository.RecommendRepository;
import lombok.Builder;

import java.util.List;

public class RecommendedByBookmark extends RecommendedBySomethingBase implements RecommendedBySomething {
    private long userId;
    private String userNickname;

    @Builder
    public RecommendedByBookmark(
            RecommendRepository repository, RecommendTypes type, long userId, String userNickname
    ) {
        super(repository, type);
        this.userId = userId;
        this.userNickname = userNickname;
    }

    @Override
    public String getDescription() {
        return userNickname + "님이 찜한 보드게임";
    }

    @Override
    public List<BoardGameThumbnailDto> getRecommendedThumbnailList() {
        return repository.getMyBookmarkedBoardGames(userId);
    }
}
