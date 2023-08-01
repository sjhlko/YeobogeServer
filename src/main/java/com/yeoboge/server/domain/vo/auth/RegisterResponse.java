package com.yeoboge.server.domain.vo.auth;

import com.yeoboge.server.domain.entity.User;
import lombok.Builder;

@Builder
public record RegisterResponse(Long id) {
    public static RegisterResponse fromEntity(User user) {
        return RegisterResponse.builder()
                .id(user.getId())
                .build();
    }
}
