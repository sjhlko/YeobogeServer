package com.yeoboge.server.repository.impl;

import com.yeoboge.server.domain.entity.Token;
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
    public void save(final Token token) {
        ValueOperations<String, String> valueOperations = redisTemplate.opsForValue();
        valueOperations.set(token.getAccessToken(), token.getRefreshToken());
        redisTemplate.expire(token.getAccessToken(), TIME_TO_LIVE, TimeUnit.SECONDS);
    }

    @Override
    public Optional<String> findByToken(final String token) {
        String refreshToken = (String) redisTemplate.opsForValue().get(token);

        if (Objects.isNull(refreshToken)) return Optional.empty();

        return Optional.of(refreshToken);
    }
}
