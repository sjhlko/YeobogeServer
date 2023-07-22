package com.yeoboge.server.repository.impl;

import com.yeoboge.server.domain.entity.RefreshToken;
import com.yeoboge.server.enums.error.AuthenticationErrorCode;
import com.yeoboge.server.handler.AppException;
import com.yeoboge.server.repository.RefreshTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Repository;

import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Repository
@RequiredArgsConstructor
public class RefreshTokenRepositoryImpl implements RefreshTokenRepository {
    private static final Long TIME_TO_LIVE = 60L * 60 * 24 * 7;
    private final RedisTemplate redisTemplate;

    @Override
    public void save(final RefreshToken token) {
        ValueOperations<String, Long> valueOperations = redisTemplate.opsForValue();
        valueOperations.set(token.getRefreshToken(), token.getUserId());
        redisTemplate.expire(token.getRefreshToken(), TIME_TO_LIVE, TimeUnit.SECONDS);
    }

    @Override
    public void delete(String accessToken) {
        if(Boolean.FALSE.equals(redisTemplate.delete(accessToken)))
            throw new AppException(AuthenticationErrorCode.CAN_NOT_UNREGISTER,AuthenticationErrorCode.CAN_NOT_UNREGISTER.getMessage());
    }

    @Override
    public Optional<RefreshToken> findIdByToken(final String token) {
        Object value = redisTemplate.opsForValue().get(token);
        Long userId = ((Integer) value).longValue();

        if (Objects.isNull(userId)) return Optional.empty();

        return Optional.of(new RefreshToken(token, userId));
    }
}
