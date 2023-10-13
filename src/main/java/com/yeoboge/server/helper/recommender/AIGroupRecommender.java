package com.yeoboge.server.helper.recommender;

import com.yeoboge.server.domain.dto.boardGame.BoardGameDetailedThumbnailDto;
import com.yeoboge.server.domain.dto.recommend.GroupRecommendationResponse;
import com.yeoboge.server.domain.vo.recommend.GroupRecommendationRequest;
import com.yeoboge.server.domain.vo.recommend.RecommendWebClientResponse;
import com.yeoboge.server.helper.utils.WebClientUtils;
import com.yeoboge.server.repository.RecommendRepository;
import lombok.Builder;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;

public class AIGroupRecommender extends AbstractGroupRecommender {
    private final String END_POINT = "/recommend/group";
    private WebClient client;

    @Builder
    public AIGroupRecommender(
            RecommendRepository repository,
            GroupRecommendationRequest request,
            List<Long> recommendableMembers,
            WebClient client
    ) {
        super(repository, request, recommendableMembers);
        this.client = client;
    }

    @Override
    public void addRecommendationsToResponse(GroupRecommendationResponse response) {
        List<Long> recommendedIds = getRecommendedBoardGamesFromAI();
        List<BoardGameDetailedThumbnailDto> recommendation =
                repository.getRecommendedBoardGamesForGroup(recommendedIds);
        response.addRecommendations(recommendation);
    }

    private List<Long> getRecommendedBoardGamesFromAI() {
        request.setRecommendableMembers(recommendableMembers);
        Mono<RecommendWebClientResponse> mono = WebClientUtils.post(
                client,
                RecommendWebClientResponse.class,
                request,
                END_POINT
        );

        return mono.block().result();
    }
}
