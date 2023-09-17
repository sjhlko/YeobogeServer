package com.yeoboge.server.domain.dto.auth;

import com.yeoboge.server.domain.entity.Genre;
import com.yeoboge.server.domain.entity.Role;
import com.yeoboge.server.domain.entity.User;
import lombok.Builder;

import java.util.HashSet;
import java.util.List;

/**
 * 회원 가입  클라이언트에서 넘겨 주는 사용자 기본 정보
 *
 * @param email 계정 이메일
 * @param password 비밀번호
 * @param nickname 사용자 닉네임
 * @param favoriteGenreIds 선호 장르 ID 리스트
 */
@Builder
public record RegisterRequest(
        String email,
        String password,
        String nickname,
        List<Long> favoriteGenreIds
) {
    /**
     * 기본 가입 정보를 통해 {@link User} 엔티티를 생성해 반환함.
     *
     * @param hashedPassword 보안을 위해 해싱한 비밀번호
     * @param favoriteGenres 선호하는 {@link Genre} Set
     * @return {@link User} 엔티티
     */
    public User toEntity(String hashedPassword, List<Genre> favoriteGenres) {
        User newUser = User.builder()
                .email(email)
                .password(hashedPassword)
                .nickname(nickname)
                .role(Role.USER)
                .favoriteGenres(new HashSet<>())
                .build();
        newUser.addFavoriteGenres(favoriteGenres);

        return newUser;
    }
}
