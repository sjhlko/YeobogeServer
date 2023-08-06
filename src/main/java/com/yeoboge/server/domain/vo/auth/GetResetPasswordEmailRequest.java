package com.yeoboge.server.domain.vo.auth;

import lombok.Builder;

/**
 * 임시 비밀번호 발급 요청시 클라이언트에서 넘겨주는 이메일을 담은 VO
 *
 * @param email 임시 비밀번호를 받을 이메일
 */
@Builder
public record GetResetPasswordEmailRequest(String email) { }
