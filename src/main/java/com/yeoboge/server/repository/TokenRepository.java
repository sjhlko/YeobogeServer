package com.yeoboge.server.repository;

import com.yeoboge.server.domain.vo.auth.Tokens;

import java.util.Optional;

public interface TokenRepository {
    void save(final Tokens token);
    void delete(final String accessToken);
    Optional<String> findByToken(final String accessToken);
}
