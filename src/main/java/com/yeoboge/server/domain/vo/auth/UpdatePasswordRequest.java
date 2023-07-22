package com.yeoboge.server.domain.vo.auth;

public record UpdatePasswordRequest(String existingPassword, String updatedPassword) { }
