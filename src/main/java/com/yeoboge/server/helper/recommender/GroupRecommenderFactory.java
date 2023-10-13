package com.yeoboge.server.helper.recommender;

import org.springframework.web.reactive.function.client.WebClient;

public interface GroupRecommenderFactory {
    GroupRecommender getRecommender(WebClient client);
}
