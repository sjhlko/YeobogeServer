package com.yeoboge.server.domain.dto.auth;

import com.yeoboge.server.domain.entity.Genre;
import com.yeoboge.server.domain.entity.Role;
import com.yeoboge.server.domain.entity.User;
import lombok.Builder;
import org.apache.commons.lang3.RandomStringUtils;

import java.util.List;
import java.util.Set;

@Builder
public record RegisterRequest(
        String email,
        String password,
        String nickname,
        List<Long> favoriteGenreIds
) {
    public User toEntity(String hashedPassword, Set<Genre> favoriteGenres) {
        String userCode = RandomStringUtils.randomAlphabetic(1) + RandomStringUtils.randomNumeric(5);
        return User.builder()
                .email(email)
                .password(hashedPassword)
                .nickname(nickname)
                .role(Role.USER)
                .userCode(userCode)
                .favoriteGenres(favoriteGenres)
                .build();
    }
}
