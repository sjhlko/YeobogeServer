package com.yeoboge.server.controller;

import com.yeoboge.server.domain.dto.auth.RegisterRequest;
import com.yeoboge.server.domain.vo.auth.*;
import com.yeoboge.server.domain.vo.response.MessageResponse;
import com.yeoboge.server.domain.vo.response.Response;
import com.yeoboge.server.service.AuthService;
import lombok.RequiredArgsConstructor;
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
        return Response.created(authService.register(request));
    }

    @GetMapping("/email-duplicate")
    public Response<com.yeoboge.server.domain.vo.response.MessageResponse> checkEmailDuplication(@RequestParam String email) {
        return Response.success(authService.checkEmailDuplication(email));
    }

    @PostMapping("/login")
    public Response<Tokens> login(@RequestBody LoginRequest request) {
        return Response.success(authService.login(request));
    }

    @PatchMapping("/logout")
    public Response<com.yeoboge.server.domain.vo.response.MessageResponse> logout(@RequestHeader("Authorization") String header) {
        return Response.success(authService.logout(header));
    }

    @PostMapping("/refresh")
    public Response<Tokens> refreshTokens(@RequestBody Tokens tokens) {
        return Response.success(authService.refreshTokens(tokens));
    }

    @PatchMapping("/temp-password")
    public Response<com.yeoboge.server.domain.vo.response.MessageResponse> getResetPasswordEmail(@RequestBody GetResetPasswordEmailRequest request) {
        com.yeoboge.server.domain.vo.response.MessageResponse messageResponse = authService.makeTempPassword(request);
        return Response.success(messageResponse);
    }

    @PatchMapping("/new-password")
    public Response<com.yeoboge.server.domain.vo.response.MessageResponse> updatePassword(@RequestBody UpdatePasswordRequest request, Authentication authentication) {
        com.yeoboge.server.domain.vo.response.MessageResponse messageResponse = authService.updatePassword(request,authentication.getPrincipal());
        return Response.success(messageResponse);
    }

    @DeleteMapping("/unregister")
    public Response<MessageResponse> unregister(Authentication authentication, @RequestHeader("Authorization") String authorizationHeader) {
        MessageResponse messageResponse = authService.unregister(authentication,authorizationHeader);
        return Response.success(messageResponse);
    }
}
