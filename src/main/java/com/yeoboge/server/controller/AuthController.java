package com.yeoboge.server.controller;

import com.yeoboge.server.domain.dto.auth.RegisterRequest;
import com.yeoboge.server.domain.vo.auth.*;
import com.yeoboge.server.domain.vo.response.Response;
import com.yeoboge.server.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auths")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<Response<RegisterResponse>> register(@RequestBody RegisterRequest request) {
        Response<RegisterResponse> response = Response.success(authService.register(request));
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PostMapping("/login")
    public Response<LoginResponse> login(@RequestBody LoginRequest request) {
        return Response.success(authService.login(request));
    }

    @PostMapping("/temp-password")
    public Response<TempPasswordResponse> getResetPasswordEmail(@RequestBody GetResetPasswordEmailRequest request) {
        TempPasswordResponse tempPasswordResponse = authService.makeTempPassword(request);
        return Response.success(tempPasswordResponse);
    }
}
