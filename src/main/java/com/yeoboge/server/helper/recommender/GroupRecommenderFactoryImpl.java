package com.yeoboge.server.helper.recommender;

import com.yeoboge.server.domain.vo.recommend.GroupRecommendationRequest;
import com.yeoboge.server.repository.RecommendRepository;
import lombok.Builder;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;

@Builder
public class GroupRecommenderFactoryImpl implements GroupRecommenderFactory {
    private RecommendRepository repository;
    private GroupRecommendationRequest request;
    private List<Long> recommendableMembers;

    @Override
    public GroupRecommender getRecommender(WebClient client) {
        return getGroupRecommenderByNumMember(recommendableMembers.size(), client);
    }

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
