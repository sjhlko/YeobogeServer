package com.yeoboge.server.service;

import com.yeoboge.server.domain.vo.auth.LoginRequest;
import com.yeoboge.server.domain.vo.auth.LoginResponse;

public interface AuthService {
    LoginResponse login(LoginRequest request);
}
