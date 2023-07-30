package com.yeoboge.server.repository.impl;

import com.yeoboge.server.domain.vo.auth.Tokens;
import com.yeoboge.server.enums.error.AuthenticationErrorCode;
import com.yeoboge.server.handler.AppException;
import com.yeoboge.server.repository.TokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Repository;

import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Repository
@RequiredArgsConstructor
public class TokenRepositoryImpl implements TokenRepository {
    private static final Long TIME_TO_LIVE = 60L * 60 * 24 * 7;
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
}
