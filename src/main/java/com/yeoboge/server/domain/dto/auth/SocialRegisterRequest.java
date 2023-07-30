package com.yeoboge.server.domain.dto.auth;

import com.yeoboge.server.domain.entity.Genre;
import com.yeoboge.server.domain.entity.Role;
import com.yeoboge.server.domain.entity.User;
import com.yeoboge.server.utils.StringGeneratorUtils;

import java.util.List;
import java.util.Set;

public record SocialRegisterRequest(String email, String nickname, List<Long> favoriteGenreIds) {
    public User toEntity(Set<Genre> favoriteGenres) {
        String userCode = StringGeneratorUtils.generateUserCode();

        return User.builder()
                .email(email)
                .nickname(nickname)
                .role(Role.USER)
                .userCode(userCode)
                .favoriteGenres(favoriteGenres)
                .build();
    }
}
