package com.yeoboge.server.helper.utils;

import com.yeoboge.server.config.security.JwtProvider;
import com.yeoboge.server.domain.vo.auth.Tokens;
import com.yeoboge.server.repository.TokenRepository;

/**
 * JWT Token 발급 관련 유틸 클래스
 */
public class JwtTokenUtils {
    /**
     * JWT 토큰을 발급하고 해당 토큰을 Redis 스토리지에 저장함.
     *
     * @param repository {@link TokenRepository}
     * @param provider {@link JwtProvider}
     * @param userId 사용자 ID
     * @return Access Token, Refresh Token을 담고 있는 {@link Tokens}
     */
    public static Tokens generateTokens(TokenRepository repository, JwtProvider provider, long userId) {
        Tokens tokens = provider.generateTokens(userId);
        repository.save(tokens);
        repository.saveValidTokens(tokens, userId);

        return tokens;
    }
}
