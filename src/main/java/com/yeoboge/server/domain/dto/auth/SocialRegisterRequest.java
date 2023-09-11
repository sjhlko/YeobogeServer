package com.yeoboge.server.domain.dto.auth;

import com.yeoboge.server.domain.entity.Genre;
import com.yeoboge.server.domain.entity.Role;
import com.yeoboge.server.domain.entity.User;

import java.util.List;
import java.util.Set;

/**
 * 소셜 로그인으로 회원 가입 시 클라이언트에서 넘겨주는 사용자 가입 정보
 *
 * @param email 소셜 계정 이메일
 * @param nickname 사용자 닉네임
 * @param favoriteGenreIds 사용자 선호 장르 ID 리스트
 */
public record SocialRegisterRequest(String email, String nickname, List<Long> favoriteGenreIds) {

    /**
     * 기본 가입 정보를 통해 User 엔티티를 생성해 반환함.
     *
     * @param favoriteGenres 선호하는 {@link Genre} Set
     * @return {@link User} 엔티티
     */
    public User toEntity(Set<Genre> favoriteGenres) {
        return User.builder()
                .email(email)
                .nickname(nickname)
                .role(Role.USER)
                .favoriteGenres(favoriteGenres)
                .build();
    }
}
