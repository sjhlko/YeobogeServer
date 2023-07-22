package com.yeoboge.server.controller;

import com.yeoboge.server.domain.dto.auth.RegisterRequest;
import com.yeoboge.server.domain.vo.auth.*;
import com.yeoboge.server.domain.vo.response.Response;
import com.yeoboge.server.service.AuthService;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
    public Response<Tokens> login(@RequestBody LoginRequest request) {
        return Response.success(authService.login(request));
    }

    @PostMapping("/refresh")
    public Response<Tokens> test(@RequestBody Tokens tokens) {
        return Response.success(authService.refreshTokens(tokens));
    }

    @GetMapping("/temp-password")
    public Response<TempPasswordResponse> resetPassword(@RequestParam String email) throws MessagingException {
        TempPasswordResponse tempPasswordResponse = authService.makeTempPassword(email);
        return Response.success(tempPasswordResponse);
    }
}
