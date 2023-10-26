package com.yeoboge.server.domain.vo.recommend;

import java.util.ArrayList;
import java.util.List;

/**
 * 그룹 추천 요청 시 추천 받을 그룹에 대한 정보를 담은 DTO
 *
 * @param members 그룹 구성원 ID 리스트
 * @param seed 추천 목록 후보로부터 랜덤으로 선택 시 seed 값으로 적용할 정수값
 */
public record GroupRecommendationRequest(List<Long> members, int seed) {
    /**
     * AI API로부터 추천 받기가 가능한 그룹 구성원으로 대체함.
     *
     * @param recommendableMembers 추천을 받을 수 있는 그룹 구성원 ID 리스트
     */
    public void setRecommendableMembers(List<Long> recommendableMembers) {
        members.clear();
        members.addAll(recommendableMembers);
    }


    /**
     * 그룹 구성원 ID 리스트에서 자신을 제외한 나머지 그룹원들의 ID 리스트만 반환함.
     *
     * @param userId 그룹원에서 제외할 사용자 ID
     * @return 사용자 ID를 제외한 그룹 구성원 ID 리스트
     */
    public List<Long> getGroupMemberId(long userId) {
        List<Long> memberIds = new ArrayList<>(members);
        memberIds.remove(userId);
        return memberIds;
    }
}
