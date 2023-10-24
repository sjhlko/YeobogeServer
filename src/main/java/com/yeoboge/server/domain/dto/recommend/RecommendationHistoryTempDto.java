package com.yeoboge.server.domain.dto.recommend;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.yeoboge.server.enums.error.CommonErrorCode;
import com.yeoboge.server.handler.AppException;

import java.util.List;

/**
 * 그룹 추천 기록을 DB 조회 시 필요한 컬럼을 매핑할 DTO
 *
 * @param groupMember 함께 추천 받은 그룹원 리스트가 직렬화된 {@code String}
 * @param createdAt 추천 받은 시각에 대한 {@code String} format
 */
public record RecommendationHistoryTempDto(
        String groupMember,
        String createdAt
) {
    /**
     * {@code String}으로 직렬화된 그룹원 ID 리스트를 {@code List}로 역직렬화함.
     *
     * @return 그룹원 ID 리스트
     */
    public List<Long> parseGroupMemberIdList() {
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.readValue(groupMember, new TypeReference<>() {});
        } catch (JsonProcessingException e) {
            throw new AppException(CommonErrorCode.INTERNAL_SERVER_ERROR);
        }
    }
}
