package com.yeoboge.server.helper.recommender;

import com.yeoboge.server.domain.vo.recommend.GroupRecommendationRequest;
import com.yeoboge.server.repository.RecommendRepository;
import lombok.Builder;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;

/**
 * {@link GroupRecommenderFactory} 구현체
 */
@Builder
public class GroupRecommenderFactoryImpl implements GroupRecommenderFactory {
    private RecommendRepository repository;
    private GroupRecommendationRequest request;
    private List<Long> recommendableMembers;

    @Override
    public GroupRecommender getRecommender(WebClient client) {
        return getGroupRecommenderByNumMember(recommendableMembers.size(), client);
    }

    /**
     * 추천 받을 수 있는 그룹 구성원 수에 따라 다른 방식으로 추천 로직을 수행하므로
     * 이를 기준으로 필요한 {@link GroupRecommender} 반환함.
     *
     * @param numRecommendableMember AI를 통해 추천 받을 수 있는 그룹 구성원 수
     * @param client 외부 API 요청 시 필요한 {@link WebClient}
     * @return {@link GroupRecommender}
     */
    private GroupRecommender getGroupRecommenderByNumMember(int numRecommendableMember, WebClient client) {
        return numRecommendableMember == 0
                ? GenreGroupRecommender.builder()
                .repository(repository)
                .request(request)
                .recommendableMembers(recommendableMembers)
                .build()
                : AIGroupRecommender.builder()
                .recommendableMembers(recommendableMembers)
                .repository(repository)
                .request(request)
                .client(client)
                .build();
    }
}
