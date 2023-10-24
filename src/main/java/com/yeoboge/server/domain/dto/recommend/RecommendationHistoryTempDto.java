package com.yeoboge.server.domain.dto.recommend;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.yeoboge.server.enums.error.CommonErrorCode;
import com.yeoboge.server.handler.AppException;

import java.util.List;

public record RecommendationHistoryTempDto(
        String groupMember,
        String createdAt
) {
    public List<Long> parseGroupMemberIdList() {
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.readValue(groupMember, new TypeReference<>() {});
        } catch (JsonProcessingException e) {
            throw new AppException(CommonErrorCode.INTERNAL_SERVER_ERROR);
        }
    }
}
