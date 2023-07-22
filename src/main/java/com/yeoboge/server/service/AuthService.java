package com.yeoboge.server.service;

import com.yeoboge.server.domain.dto.auth.RegisterRequest;
import com.yeoboge.server.domain.vo.auth.*;
import org.springframework.security.core.Authentication;

public interface AuthService {
    RegisterResponse register(RegisterRequest request);
    LoginResponse login(LoginRequest request);
    TempPasswordResponse makeTempPassword(GetResetPasswordEmailRequest request);
    UpdatePasswordResponse updatePassword(UpdatePasswordRequest request, Object principal);
    UnregisterResponse unregister(Authentication authentication, String authorizationHeader);
}
