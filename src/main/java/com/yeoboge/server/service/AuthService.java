package com.yeoboge.server.service;

import com.yeoboge.server.domain.dto.auth.RegisterRequest;
import com.yeoboge.server.domain.vo.auth.LoginRequest;
import com.yeoboge.server.domain.vo.auth.LoginResponse;
import com.yeoboge.server.domain.vo.auth.RegisterResponse;
import com.yeoboge.server.domain.vo.auth.TempPasswordResponse;
import jakarta.mail.MessagingException;

public interface AuthService {
    RegisterResponse register(RegisterRequest request);
    LoginResponse login(LoginRequest request);
    TempPasswordResponse makeTempPassword(String email) throws MessagingException;
}
