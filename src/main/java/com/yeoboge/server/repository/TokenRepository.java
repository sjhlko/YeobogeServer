package com.yeoboge.server.repository;

import com.yeoboge.server.domain.entity.Token;

import java.util.Optional;

public interface TokenRepository {
    void save(final Token token);
    Optional<String> findByToken(final String accessToken);
}
