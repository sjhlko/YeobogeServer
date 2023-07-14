package com.yeoboge.server.service;

import com.yeoboge.server.vo.auth.LoginRequest;
import com.yeoboge.server.vo.auth.LoginResponse;

public interface AuthService {
    LoginResponse login(LoginRequest request);
}
