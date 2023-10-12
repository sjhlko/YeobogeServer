package com.yeoboge.server.helper.recommender;

import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;

public interface RecommenderFactory {
    List<Recommender> getRecommenders(WebClient client);
}
