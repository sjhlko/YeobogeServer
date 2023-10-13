package com.yeoboge.server.helper.recommender;

import com.yeoboge.server.domain.dto.boardGame.BoardGameDetailedThumbnailDto;
import com.yeoboge.server.domain.dto.recommend.GroupRecommendationResponse;
import com.yeoboge.server.domain.entity.Genre;
import com.yeoboge.server.domain.vo.recommend.GroupRecommendationRequest;
import com.yeoboge.server.repository.RecommendRepository;
import lombok.Builder;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class GenreGroupRecommender extends AbstractGroupRecommender {
    @Builder
    public GenreGroupRecommender(
            RecommendRepository repository,
            GroupRecommendationRequest request,
            List<Long> recommendableMembers
    ) {
        super(repository, request, recommendableMembers);
    }

    @Override
    public void addRecommendationsToResponse(GroupRecommendationResponse response) {
        long mostFavoriteGenreId = getGroupMostFavoriteGenre();
        List<BoardGameDetailedThumbnailDto> recommendation =
                repository.getPopularBoardGamesOfGenreForGroup(mostFavoriteGenreId);
        response.addRecommendations(recommendation);
    }

    private long getGroupMostFavoriteGenre() {
        Map<Long, Integer> favoriteGenresOfMember = request.members().stream()
                .flatMap(memberId -> repository.getMyFavoriteGenre(memberId).stream())
                .collect(Collectors.toMap(
                        Genre::getId,
                        genre -> 1,
                        Integer::sum
                ));
        Optional<Map.Entry<Long, Integer>> mostFavoriteGenreEntry = favoriteGenresOfMember.entrySet()
                .stream()
                .max(Map.Entry.comparingByValue());

        return mostFavoriteGenreEntry.get().getKey();
    }
}
