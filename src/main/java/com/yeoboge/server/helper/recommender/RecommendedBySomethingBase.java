package com.yeoboge.server.helper.recommender;

import com.yeoboge.server.enums.RecommendTypes;
import com.yeoboge.server.repository.RecommendRepository;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class RecommendedBySomethingBase {
    protected RecommendRepository repository;
    protected RecommendTypes type;

    public String getKey() {
        return type.getKey();
    }
}
