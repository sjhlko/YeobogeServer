package com.yeoboge.server.config.security;

public interface JwtProvider {
    String generateAccessToken(Long userId);
    String generateRefreshToken(Long userId);
    boolean isValid(String token, Long userId);
    Long parseUserId(String token);
}
