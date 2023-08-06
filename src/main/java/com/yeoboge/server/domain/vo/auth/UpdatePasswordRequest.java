package com.yeoboge.server.domain.vo.auth;

import lombok.Builder;

/**
 * 비밀번호 변경 요청 시 클라이언트가 넘겨주는 변경 전 비밀번호와 새로운 비밀번호를 담은 VO
 *
 * @param existingPassword 변경 전 비밀번호
 * @param updatedPassword 변경 하고자 하는 새로운 비밀번호
 */
@Builder
public record UpdatePasswordRequest(String existingPassword, String updatedPassword) { }
