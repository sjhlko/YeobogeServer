package com.yeoboge.server.service.impl;

import com.yeoboge.server.security.JwtProvider;
import com.yeoboge.server.vo.auth.LoginRequest;
import com.yeoboge.server.vo.auth.LoginResponse;
import com.yeoboge.server.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
    private final AuthenticationManager authManager;
    private final JwtProvider jwtProvider;

    @Override
    public LoginResponse login(LoginRequest request) {
        String username = request.getEmail();
        String password = request.getPassword();
        authenticate(username, password);

        return generateToken(username);
    }

    private void authenticate(String username, String password) {
        UsernamePasswordAuthenticationToken authToken =
                new UsernamePasswordAuthenticationToken(username, password);
        authManager.authenticate(authToken);
    }

    private LoginResponse generateToken(String username) {
        String accessToken = jwtProvider.generateAccessToken(username);
        String refreshToken = jwtProvider.generateRefreshToken(username);
        return new LoginResponse(accessToken, refreshToken);
    }
}
