package com.yeoboge.server.config.security;

import com.yeoboge.server.domain.vo.auth.Tokens;

/**
 * JWT 토큰 발급 관련 메서드를 제공하는 인터페이스
 */
public interface JwtProvider {
    /**
     * {@link com.yeoboge.server.domain.entity.User} ID를 토큰 페이로드에 담아 발급함.
     *
     * @param userId {@link com.yeoboge.server.domain.entity.User} ID
     * @return Access Token, Refresh Token을 담고 있는 {@link Tokens}
     */
    Tokens generateTokens(Long userId);

    /**
     * JWT 토큰의 유효성을 검증함.
     *
     * @param token JWT 토큰
     * @param userId 토큰 페이로드에 포함된 {@link com.yeoboge.server.domain.entity.User} ID와 비교할 ID
     * @return 페이로드의 ID와 {@code userId} 값이 일치하면 true, 아니면 false
     */
    boolean isValid(String token, Long userId);

    /**
     * JWT 페이로드에 포함된 사용자 ID를 추출함.
     *
     * @param token JWT 토큰
     * @return 토큰 페이로드에 담겨 있던 {@link com.yeoboge.server.domain.entity.User} ID
     */
    Long parseUserId(String token);
}
