package com.yeoboge.server.helper.recommender;

import com.yeoboge.server.enums.RecommendTypes;
import com.yeoboge.server.helper.utils.WebClientUtils;
import com.yeoboge.server.repository.RecommendRepository;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

public abstract class RecommendedByML<T> extends RecommendedBySomethingBase {
    private WebClient client;
    protected T requestBody;

    RecommendedByML(RecommendRepository repository, RecommendTypes type, WebClient client) {
        super(repository, type);
        this.client = client;
    }

    protected <E> Mono<E> getWebClientMono(Class<E> responseClass, String endpoint) {
        return WebClientUtils.post(client, responseClass, requestBody, endpoint);
    }
}
