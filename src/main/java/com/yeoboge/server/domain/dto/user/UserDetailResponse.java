package com.yeoboge.server.domain.dto.user;

import com.yeoboge.server.domain.entity.User;
import lombok.Builder;

@Builder
public record UserDetailResponse(
        String email,
        String nickname,
        String userCode,
        String profileImagePath
) {
    public static UserDetailResponse of(User user){
        return UserDetailResponse.builder()
                .email(user.getEmail())
                .nickname(user.getNickname())
                .userCode(user.getUserCode())
                .profileImagePath(user.getProfileImagePath())
                .build();
    }

}
