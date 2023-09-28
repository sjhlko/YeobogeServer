package com.yeoboge.server.domain.dto.recommend;

import com.yeoboge.server.domain.dto.user.UserInfoDto;
import lombok.Builder;

import java.util.List;

/**
 * 그룹 구성원들의 {@link UserInfoDto}를 리스트로 전달하는 DTO
 * @param group 그룹 구성원들의 {@link UserInfoDto} 리스트
 */
@Builder
public record GroupMembersResponse(List<UserInfoDto> group) {
}
