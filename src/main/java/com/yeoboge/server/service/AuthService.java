package com.yeoboge.server.service;

import com.yeoboge.server.domain.dto.auth.RegisterRequest;
import com.yeoboge.server.domain.vo.auth.*;
import com.yeoboge.server.domain.vo.response.MessageResponse;
import org.springframework.security.core.Authentication;

public interface AuthService {
    RegisterResponse register(RegisterRequest request);
    Tokens login(LoginRequest request);
    MessageResponse logout(String header);
    TempPasswordResponse makeTempPassword(GetResetPasswordEmailRequest request);
    UpdatePasswordResponse updatePassword(UpdatePasswordRequest request, Object principal);
    UnregisterResponse unregister(Authentication authentication, String authorizationHeader);
    Tokens refreshTokens(Tokens tokens);
}
