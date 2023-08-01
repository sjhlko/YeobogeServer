package com.yeoboge.server.domain.vo.auth;

import com.yeoboge.server.domain.entity.User;
import lombok.Builder;

/**
 * 회원 가입 후 해당 사용자의 ID를 클라이언트에 전달하기 위한 VO
 * @param id {@link User} ID
 */
@Builder
public record RegisterResponse(Long id) {
    public static RegisterResponse fromEntity(User user) {
        return RegisterResponse.builder()
                .id(user.getId())
                .build();
    }
}
