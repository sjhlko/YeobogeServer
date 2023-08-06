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
        RegisterResponse response = authService.register(request);
        return Response.created(response);
    }

    /**
     * 이메일 중복 여부 확인 API
     *
     * @param email 중복 여부를 확인할 계정 이메일
     * @return 해당 이메일이 중복되지 않을 경우 HTTP 200 응답
     */
    @GetMapping("/email-duplicate")
    public Response<MessageResponse> checkEmailDuplication(@RequestParam String email) {
        MessageResponse response = authService.checkEmailDuplication(email);
        return Response.success(response);
    }

    /**
     * 로그인 API
     *
     * @param request 로그인할 계정의 {@link LoginRequest} VO
     * @return 발급된 Access Token, Refresh Token을 포함한 HTTP 200 응답
     */
    @PostMapping("/login")
    public Response<Tokens> login(@RequestBody LoginRequest request) {
        Tokens response = authService.login(request);
        return Response.success(response);
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
        MessageResponse response = authService.logout(header);
        return Response.success(response);
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
        Tokens response = authService.refreshTokens(tokens);
        return Response.success(response);
    }

    /**
     * 임시 비밀번호 메일을 보내는 API
     *
     * @param request 임시 비밀번호를 전송받을 이메일 {@link GetResetPasswordEmailRequest}
     * @return 이메일 발송됨 메세지를 포함한 HTTP 200 응답
     * @see MessageResponse
     */
    @PatchMapping("/temp-password")
    public Response<MessageResponse> getResetPasswordEmail(@RequestBody GetResetPasswordEmailRequest request) {
        MessageResponse messageResponse = authService.makeTempPassword(request);
        return Response.success(messageResponse);
    }

    /**
     * 비밀번호를 변경하는 API
     *
     * @param request 현재 비밀번호와 변경할 비밀번호에 대한 {@link UpdatePasswordRequest} VO
     * @param id 현재 로그인한 회원의 인덱스
     * @return 비밀번호 변경 성공됨 메세지를 포함한 HTTP 200 응답
     * @see MessageResponse
     */
    @SecurityRequirement(name = "Bearer Authentication")
    @PatchMapping("/new-password")
    public Response<MessageResponse> updatePassword(@RequestBody UpdatePasswordRequest request,
                                                    @AuthenticationPrincipal Long id) {
        MessageResponse messageResponse = authService.updatePassword(request, id);
        return Response.success(messageResponse);
    }

    /**
     * 회원 탈퇴 API
     *
     * @param id 현재 로그인한 회원의 인덱스
     * @param header 탈퇴할 사용자의 Access Token 값을 가진 HTTP Header
     * @return 회원 탈퇴 성공됨 메세지를 포함한 HTTP 200 응답
     * @see MessageResponse
     */
    @SecurityRequirement(name = "Bearer Authentication")
    @DeleteMapping("/unregister")
    public Response<MessageResponse> unregister(@AuthenticationPrincipal Long id,
                                                @RequestHeader("Authorization") String header) {
        MessageResponse messageResponse = authService.unregister(id, header);
        return Response.success(messageResponse);
    }
}
