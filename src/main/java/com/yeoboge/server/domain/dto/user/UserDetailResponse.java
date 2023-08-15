package com.yeoboge.server.domain.dto.user;

import com.yeoboge.server.domain.entity.User;
import lombok.Builder;

/**
 * 회원 정보 조회 요청 시 넘겨주는 회원의 정보
 *
 * @param nickname 회원 닉네임
 * @param profileImagePath 회원의 프로필 사진 링크
 */
@Builder
public record UserDetailResponse(String nickname, String profileImagePath) {
    /**
     * {@link User} 엔티티를 통해 {@link UserDetailResponse} DTO 를 생성해 반환함.
     *
     * @param user 정보를 반환할 회원의 {@link User} 엔티티
     * @return {@link UserDetailResponse} DTO
     */
    public static UserDetailResponse of(User user){
        return UserDetailResponse.builder()
                .nickname(user.getNickname())
                .profileImagePath(user.getProfileImagePath())
                .build();
    }

}
