package com.yeoboge.server.service;

import com.yeoboge.server.domain.dto.auth.RegisterRequest;
import com.yeoboge.server.domain.vo.auth.*;

public interface AuthService {
    RegisterResponse register(RegisterRequest request);
    Tokens login(LoginRequest request);
    TempPasswordResponse makeTempPassword(GetResetPasswordEmailRequest request);
    Tokens refreshTokens(Tokens tokens);
}
