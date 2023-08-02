package com.yeoboge.server.domain.dto.user;

import com.yeoboge.server.domain.entity.Genre;
import com.yeoboge.server.domain.entity.User;
import lombok.Builder;

/**
 * 회원 정보 조회 요청 시 넘겨주는 회원의 정보
 *
 * @param email 계정 이메일
 * @param nickname 회원 닉네임
 * @param userCode 회원에게 공개되는 회원 식별을 위한 고유 번호
 * @param profileImagePath 회원의 프로필 사진 링크
 */
@Builder
public record UserDetailResponse(
        String email,
        String nickname,
        String userCode,
        String profileImagePath
) {
    /**
     * {@link User} 엔티티를 통해 {@link UserDetailResponse} DTO 를 생성해 반환함.
     *
     * @param user 정보를 반환할 회원의 {@link User} 엔티티
     * @return {@link UserDetailResponse} DTO
     */
    public static UserDetailResponse of(User user){
        return UserDetailResponse.builder()
                .email(user.getEmail())
                .nickname(user.getNickname())
                .userCode(user.getUserCode())
                .profileImagePath(user.getProfileImagePath())
                .build();
    }

}
