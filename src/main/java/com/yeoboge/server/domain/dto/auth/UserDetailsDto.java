package com.yeoboge.server.domain.dto.auth;

import com.yeoboge.server.domain.entity.Role;

/**
 * Spring Security에서 인증 시 사용하는 {@link org.springframework.security.core.userdetails.UserDetails}에
 * 해당하는 컬럼과 PK 값을 포함한 엔티티 정보를 담을 DTO
 *
 * @param id 사용자 ID
 * @param email 계정 이메일
 * @param password 계정 비밀번호
 * @param role 계정 권한 {@link Role}
 */
public record UserDetailsDto(long id, String email, String password, Role role) { }
