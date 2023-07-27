package com.yeoboge.server.service;

import com.yeoboge.server.domain.dto.auth.RegisterRequest;
import com.yeoboge.server.domain.vo.auth.*;
import com.yeoboge.server.domain.vo.response.MessageResponse;
import org.springframework.security.core.Authentication;

public interface AuthService {
    RegisterResponse register(RegisterRequest request);
    com.yeoboge.server.domain.vo.response.MessageResponse checkEmailDuplication(String email);
    Tokens login(LoginRequest request);
    com.yeoboge.server.domain.vo.response.MessageResponse logout(String header);
    com.yeoboge.server.domain.vo.response.MessageResponse makeTempPassword(GetResetPasswordEmailRequest request);
    com.yeoboge.server.domain.vo.response.MessageResponse updatePassword(UpdatePasswordRequest request, Object principal);
    MessageResponse unregister(Authentication authentication, String authorizationHeader);
    Tokens refreshTokens(Tokens tokens);
}
