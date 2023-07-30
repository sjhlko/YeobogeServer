package com.yeoboge.server.config.security;

import com.yeoboge.server.domain.vo.auth.Tokens;

public interface JwtProvider {
    Tokens generateTokens(Long userId);
    boolean isValid(String token, Long userId);
    Long parseUserId(String token);
}
