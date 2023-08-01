package com.yeoboge.server.domain.dto.auth;

import com.yeoboge.server.domain.entity.Role;

public record UserDetailsDto(String email, String password, Role role) { }
