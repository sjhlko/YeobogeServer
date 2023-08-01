package com.yeoboge.server.domain.vo.auth;

import lombok.Builder;

/**
 * JWT 토큰 값을 담는 VO
 *
 * @param accessToken API 요청 시 사용자 인증에 사용되는 토큰
 * @param refreshToken Access Token 재발급 시 사용되는 토큰
 */
@Builder
public record Tokens(String accessToken, String refreshToken) { }
