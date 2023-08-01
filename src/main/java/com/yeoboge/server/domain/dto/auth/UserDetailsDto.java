package com.yeoboge.server.domain.dto.auth;

import com.yeoboge.server.domain.entity.Role;

/**
 * Spring Security에서 인증 시 사용하는 {@link org.springframework.security.core.userdetails.UserDetails}의
 * 값에 해당하는 엔티티 정보를 담을 DTO
 *
 * @param email 계정 이메일
 * @param password 계정 비밀번호
 * @param role 계정 권한 {@link Role}
 */
public record UserDetailsDto(String email, String password, Role role) { }
