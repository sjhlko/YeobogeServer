package com.yeoboge.server.helper.recommender;

import com.yeoboge.server.domain.dto.boardGame.BoardGameThumbnailDto;
import com.yeoboge.server.enums.RecommendTypes;
import com.yeoboge.server.repository.RecommendRepository;
import lombok.Builder;

import java.util.List;

public class RecommendedByGenre extends RecommendedBySomethingBase implements RecommendedBySomething {
    private long genreId;
    private String userNickname;
    private String genreName;

    @Builder
    public RecommendedByGenre(
            RecommendRepository repository, RecommendTypes type, long genreId, String userNickname, String genreName
    ) {
        super(repository, type);
        this.genreId = genreId;
        this.userNickname = userNickname;
        this.genreName = genreName;
    }

    @Override
    public String getKey() {
        return type.getKey() + genreName;
    }

    @Override
    public String getDescription() {
        return userNickname + "님이 좋아하는 " + genreName + " 보드게임";
    }

    @Override
    public List<BoardGameThumbnailDto> getRecommendedThumbnailList() {
        return repository.getPopularBoardGamesOfFavoriteGenre(genreId);
    }
}
