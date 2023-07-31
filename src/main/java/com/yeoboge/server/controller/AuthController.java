package com.yeoboge.server.controller;

import com.yeoboge.server.domain.dto.auth.RegisterRequest;
import com.yeoboge.server.domain.vo.auth.*;
import com.yeoboge.server.domain.vo.response.MessageResponse;
import com.yeoboge.server.domain.vo.response.Response;
import com.yeoboge.server.service.AuthService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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
    public Response<MessageResponse> checkEmailDuplication(@RequestParam String email) {
        return Response.success(authService.checkEmailDuplication(email));
    }

    @PostMapping("/login")
    public Response<Tokens> login(@RequestBody LoginRequest request) {
        return Response.success(authService.login(request));
    }

    @SecurityRequirement(name = "Bearer Authentication")
    @PatchMapping("/logout")
    public Response<MessageResponse> logout(@RequestHeader("Authorization") String header) {
        return Response.success(authService.logout(header));
    }

    @SecurityRequirement(name = "Bearer Authentication")
    @PostMapping("/refresh")
    public Response<Tokens> refreshTokens(@RequestBody Tokens tokens) {
        return Response.success(authService.refreshTokens(tokens));
    }

    @PatchMapping("/temp-password")
    public Response<MessageResponse> getResetPasswordEmail(@RequestBody GetResetPasswordEmailRequest request) {
        MessageResponse messageResponse = authService.makeTempPassword(request);
        return Response.success(messageResponse);
    }

    @SecurityRequirement(name = "Bearer Authentication")
    @PatchMapping("/new-password")
    public Response<MessageResponse> updatePassword(@RequestBody UpdatePasswordRequest request, @AuthenticationPrincipal Long id) {
        MessageResponse messageResponse = authService.updatePassword(request,id);
        return Response.success(messageResponse);
    }

    @SecurityRequirement(name = "Bearer Authentication")
    @DeleteMapping("/unregister")
    public Response<MessageResponse> unregister(@AuthenticationPrincipal Long id, @RequestHeader("Authorization") String authorizationHeader) {
        MessageResponse messageResponse = authService.unregister(id,authorizationHeader);
        return Response.success(messageResponse);
    }
}
