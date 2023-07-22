package com.yeoboge.server.controller;

import com.yeoboge.server.domain.dto.auth.RegisterRequest;
import com.yeoboge.server.domain.vo.auth.*;
import com.yeoboge.server.domain.vo.response.Response;
import com.yeoboge.server.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
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
    public Response<Tokens> refreshTokens(@RequestBody Tokens tokens) {
        return Response.success(authService.refreshTokens(tokens));
    }

    @PatchMapping("/temp-password")
    public Response<TempPasswordResponse> resetPassword(@RequestBody GetResetPasswordEmailRequest request) {
        TempPasswordResponse tempPasswordResponse = authService.makeTempPassword(request);
        return Response.success(tempPasswordResponse);
    }

    @PatchMapping("/new-password")
    public Response<UpdatePasswordResponse> updatePassword(@RequestBody UpdatePasswordRequest request, Authentication authentication) {
        UpdatePasswordResponse updatePasswordResponse = authService.updatePassword(request,authentication.getPrincipal());
        return Response.success(updatePasswordResponse);
    }

    @DeleteMapping("/unregister")
    public Response<UnregisterResponse> unregister(Authentication authentication, @RequestHeader("Authorization") String authorizationHeader) {
        UnregisterResponse unregisterResponse  = authService.unregister(authentication,authorizationHeader);
        return Response.success(unregisterResponse);
    }
}
