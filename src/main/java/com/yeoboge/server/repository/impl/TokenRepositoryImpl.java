package com.yeoboge.server.repository.impl;

import com.yeoboge.server.domain.vo.auth.Tokens;
import com.yeoboge.server.enums.error.AuthenticationErrorCode;
import com.yeoboge.server.handler.AppException;
import com.yeoboge.server.repository.TokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Repository;

import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

/**
 * {@link TokenRepository} 구현체
 */
@Repository
@RequiredArgsConstructor
public class TokenRepositoryImpl implements TokenRepository {
    private static final Long TIME_TO_LIVE = 60L * 60 * 24 * 7;
    private static final String AUTH_TOKEN_PREFIX = "TOKENS_";
    private final RedisTemplate redisTemplate;

    @Override
    public void save(final Tokens token) {
        ValueOperations<String, String> valueOperations = redisTemplate.opsForValue();
        valueOperations.set(token.accessToken(), token.refreshToken());
        redisTemplate.expire(token.accessToken(), TIME_TO_LIVE, TimeUnit.SECONDS);
    }

    @Override
    public void delete(final String accessToken) {
        if (Boolean.FALSE.equals(redisTemplate.delete(accessToken)))
            throw new AppException(AuthenticationErrorCode.TOKEN_INVALID);
    }

    @Override
    public Optional<String> findByToken(final String token) {
        String refreshToken = (String) redisTemplate.opsForValue().get(token);

        if (Objects.isNull(refreshToken)) return Optional.empty();

        return Optional.of(refreshToken);
    }

    @Override
    public void saveValidTokens(Tokens tokens, long id) {
        HashOperations<String, String, String> hashOperations = redisTemplate.opsForHash();
        String key = getCurrentTokenKey(id);

        hashOperations.put(key, "access_token", tokens.accessToken());
        hashOperations.put(key, "refresh_token", tokens.refreshToken());
        redisTemplate.expire(key, TIME_TO_LIVE, TimeUnit.SECONDS);
    }

    @Override
    public void deleteValidTokens(long id) {
        String key = getCurrentTokenKey(id);
        if (Boolean.FALSE.equals(redisTemplate.delete(key)))
            throw new AppException(AuthenticationErrorCode.USER_NOT_FOUND);
    }

    @Override
    public Optional<Tokens> findValidTokens(long id) {
        String key = getCurrentTokenKey(id);
        String accessToken = (String) redisTemplate.opsForHash().get(key, "access_token");
        String refreshToken = (String) redisTemplate.opsForHash().get(key, "refresh_token");

        if (Objects.isNull(accessToken)) return Optional.empty();

        return Optional.ofNullable(
                Tokens.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build()
        );
    }

    @Override
    public void saveFcmToken(final Long id, final String token) {
        ValueOperations<String, String> valueOperations = redisTemplate.opsForValue();
        valueOperations.set(id.toString(), token);
    }

    @Override
    public void deleteFcmToken(Long id) {
        if (Boolean.FALSE.equals(redisTemplate.delete(id.toString())))
            throw new AppException(AuthenticationErrorCode.TOKEN_INVALID);
    }

    @Override
    public Optional<String> findFcmToken(final Long id) {
        String refreshToken = (String) redisTemplate.opsForValue().get(id.toString());
        if (Objects.isNull(refreshToken)) return Optional.empty();
        return Optional.of(refreshToken);
    }

    /**
     * 사용자의 유효한 Token을 저장하기 위한 Redis Hash Key를 생성함.
     *
     * @param id 사용자 ID
     * @return 해당 사용자의 Token 저장 Redis Hash Key
     */
    private String getCurrentTokenKey(long id) {
        return String.format("%s%d", AUTH_TOKEN_PREFIX, id);
    }
}
