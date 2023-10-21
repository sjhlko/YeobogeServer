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

/**
 * 평가 데이터가 쌓이지 않아 AI 추천을 받지 못하는
 * 그룹을 위해 선호하는 장르를 통해 추천 목록을 생성하는 로직을 구현한 {@link GroupRecommender} 구현체
 */
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

    /**
     * 추천을 요청한 그룹 구성원 중 가장 많은 인원이 선호하는 장르 ID를 반환함.
     *
     * @return 그룹 구성원들이 가장 많이 선호하는 장르 ID
     */
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
