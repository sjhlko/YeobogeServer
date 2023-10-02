package com.yeoboge.server.domain.dto.recommend;

import com.yeoboge.server.domain.dto.user.UserInfoDto;
import lombok.Builder;
import lombok.Getter;

import java.util.*;

/**
 * 그룹 구성원들의 {@link UserInfoDto}를 리스트로 전달하는 DTO
 */
@Getter
public class GroupMembersResponse {
    private List<UserInfoDto> group;
    private int seed;

    @Builder
    GroupMembersResponse(List<UserInfoDto> group) {
        this.group = group;
        setSeed();
    }

    /**
     * 그룹 구성원마다 동일한 seed 값으로 추천 결과를 생성하기 위해
     * 그룹원 정보를 기준으로 random seed 값을 생성함.
     */
    private void setSeed() {
        long idSeed = 0;
        for (UserInfoDto dto : group)
            idSeed += dto.id();

        this.seed = (new Random(idSeed)).nextInt(Integer.MAX_VALUE);
    }
}
