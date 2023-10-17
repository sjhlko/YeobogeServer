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

/**
 * 보드게임 평가 데이터가 쌓인 그룹 구성원들에 대해
 * AI 모델을 통해 추천 목록을 생성하는 로직을 구현한 {@link GroupRecommender} 구현체
 */
public class AIGroupRecommender extends AbstractGroupRecommender {
    private final String END_POINT = "/recommends/group";
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

    /**
     * 외부 AI API로 추천 요청을 보내 응답 결과를 반환함.
     *
     * @return AI API로 부터 응답 받은 추천 보드게임 ID 리스트
     */
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
