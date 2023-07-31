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

/**
 * 계정 관련 API 엔드포인트에 대해 매핑되는 Rest Controller
 *
 * @author Seo Jeonghee, Yoon Soobin
 */
@RestController
@RequestMapping("/auths")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    /**
     * 회원 가입 API
     *
     * @param request 회원 가입하는 사용자의 {@link RegisterRequest} DTO
     * @return HTTP 201 응답
     */
    @PostMapping("/register")
    public ResponseEntity<Response<RegisterResponse>> register(@RequestBody RegisterRequest request) {
        return Response.created(authService.register(request));
    }

    /**
     * 이메일 중복 여부 확인 API
     *
     * @param email 중복 여부를 확인할 계정 이메일
     * @return 해당 이메일이 중복되지 않을 경우 HTTP 200 응답
     */
    @GetMapping("/email-duplicate")
    public Response<MessageResponse> checkEmailDuplication(@RequestParam String email) {
        return Response.success(authService.checkEmailDuplication(email));
    }

    /**
     * 로그인 API
     *
     * @param request 로그인할 계정의 {@link LoginRequest} VO
     * @return 발급된 Access Token, Refresh Token을 포함한 HTTP 200 응답
     */
    @PostMapping("/login")
    public Response<Tokens> login(@RequestBody LoginRequest request) {
        return Response.success(authService.login(request));
    }

    /**
     * 로그아웃 API
     *
     * @param header 로그아웃할 사용자의 Access Token 값을 가진 HTTP Header
     * @return 로그아웃 성공 메세지를 포함한 HTTP 200 응답
     * @see MessageResponse
     */
    @SecurityRequirement(name = "Bearer Authentication")
    @PatchMapping("/logout")
    public Response<MessageResponse> logout(@RequestHeader("Authorization") String header) {
        return Response.success(authService.logout(header));
    }

    /**
     * 사용자의 만료된 Access Token으로 Access Token, Refresh Token 재발급 API
     *
     * @param tokens 재발급 검증에 사용할 Access Token, Refresh Token을 포함한 {@link Tokens}
     * @return 재발급된 Access Token, Refresh Token을 포함한 HTTP 200 응답
     */
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
