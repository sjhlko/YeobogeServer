package com.yeoboge.server.service;

import com.yeoboge.server.domain.dto.auth.RegisterRequest;
import com.yeoboge.server.domain.vo.auth.*;
import com.yeoboge.server.domain.vo.response.MessageResponse;

public interface AuthService {
    RegisterResponse register(RegisterRequest request);
    MessageResponse checkEmailDuplication(String email);
    Tokens login(LoginRequest request);
    MessageResponse logout(String header);
    MessageResponse makeTempPassword(GetResetPasswordEmailRequest request);
    MessageResponse updatePassword(UpdatePasswordRequest request, Long id);
    MessageResponse unregister(Long id, String authorizationHeader);
    Tokens refreshTokens(Tokens tokens);
}
