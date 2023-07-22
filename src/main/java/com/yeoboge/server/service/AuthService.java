package com.yeoboge.server.service;

import com.yeoboge.server.domain.dto.auth.RegisterRequest;
import com.yeoboge.server.domain.vo.auth.*;
import jakarta.mail.MessagingException;

public interface AuthService {
    RegisterResponse register(RegisterRequest request);
    Tokens login(LoginRequest request);
    TempPasswordResponse makeTempPassword(String email) throws MessagingException;
    Tokens refreshTokens(Tokens tokens);
}
