package com.yeoboge.server.domain.dto.recommend;

import com.yeoboge.server.domain.dto.user.UserInfoDto;
import lombok.Builder;

import java.util.List;

@Builder
public record GroupMembersResponse(List<UserInfoDto> group) {
}
