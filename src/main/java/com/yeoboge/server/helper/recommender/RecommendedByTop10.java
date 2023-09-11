package com.yeoboge.server.helper.recommender;

import com.yeoboge.server.domain.dto.boardGame.BoardGameThumbnailDto;
import com.yeoboge.server.enums.RecommendTypes;
import com.yeoboge.server.repository.RecommendRepository;
import lombok.Builder;

import java.util.List;

public class RecommendedByTop10 extends RecommendedBySomethingBase implements RecommendedBySomething {
    @Builder
    public RecommendedByTop10(RecommendRepository repository, RecommendTypes type) {
        super(repository, type);
    }

    @Override
    public String getDescription() {
        return "실시간 인기 보드게임 TOP 10";
    }

    @Override
    public List<BoardGameThumbnailDto> getRecommendedThumbnailList() {
        return repository.getTopTenBoardGames();
    }
}
