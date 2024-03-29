package com.yeoboge.server.domain.dto.user;

import com.yeoboge.server.domain.entity.User;
import lombok.Builder;

/**
 * 친구 목록 조회 시 필요한 엔티티의 데이터를 담기 위한 DTO
 *
 * @param id 회원 ID
 * @param nickname 회원 닉네임
 * @param imagePath 회원 프로필 이미지 링크
 */
@Builder
public record UserInfoDto(Long id, String nickname, String imagePath) {
    public static UserInfoDto of(User user){
        return UserInfoDto.builder()
                .id(user.getId())
                .nickname(user.getNickname())
                .imagePath(user.getProfileImagePath())
                .build();
    }
}
