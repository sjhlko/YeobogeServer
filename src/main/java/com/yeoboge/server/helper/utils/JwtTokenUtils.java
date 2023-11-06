package com.yeoboge.server.helper.utils;

import com.yeoboge.server.config.security.JwtProvider;
import com.yeoboge.server.domain.vo.auth.Tokens;
import com.yeoboge.server.repository.TokenRepository;

/**
 * JWT Token 관련 유틸 클래스
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

    /**
     * 현재 사용자의 Access Token이 유효한 Access Token인지 확인함.
     *
     * @param repository {@link TokenRepository}
     * @param currentToken 현재 사용자의 Access Token
     * @param userId 사용자 ID
     * @param exception 유효하지 않은 오류일 시 throw 할 {@code Exception}
     */
    public static void checkTokenValidation(
            TokenRepository repository,
            String currentToken,
            long userId,
            RuntimeException exception) {
        Tokens validTokens = repository.getValidTokens(userId);
        if (!validTokens.accessToken().equals(currentToken)) throw exception;
    }
}
