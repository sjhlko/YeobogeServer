package com.yeoboge.server.domain.dto.user;

import lombok.Builder;

/**
 * 회원 프로필 변경 시 클라이언트에서 넘겨 주는 정보
 *
 * @param isChanged 회원의 프로필 사진 변경 여부
 * @param nickname 회원 닉네임
 */
@Builder
public record UserUpdateRequest(boolean isChanged, String nickname) { }
