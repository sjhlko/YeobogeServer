package com.yeoboge.server.controller;

import com.yeoboge.server.domain.dto.auth.SocialRegisterRequest;
import com.yeoboge.server.domain.vo.auth.Tokens;
import com.yeoboge.server.domain.vo.response.Response;
import com.yeoboge.server.service.OAuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 구글 로그인을 통해 접속한 회원을 가입하거나 로그인하는
 * API 엔드포인트에 대해 매핑되는 Rest Controller
 *
 * @author Yoon Soobin
 */
@RestController
@RequestMapping("/oauths")
@RequiredArgsConstructor
public class OAuthController {
    private final OAuthService oAuthService;

    /**
     * 소셜 계정 이메일로 회원 가입하는 API
     *
     * @param request 구글 로그인을 통해 회원 가입하는 사용자의 {@link SocialRegisterRequest} DTO
     * @return 발급된 Access Token, Refresh Token을 포함한 HTTP 201 응답
     */
    @PostMapping("/register")
    public ResponseEntity<Response<Tokens>> socialRegister(@RequestBody SocialRegisterRequest request) {
        return Response.created(oAuthService.socialRegister(request));
    }

    /**
     * 소셜 계정 이메일로 로그인하는 API
     *
     * @param email 회원의 구글 이메일
     * @return 발급된 Access Token, Refresh Token을 포함한 HTTP 200 응답
     */
    @PostMapping("/login")
    public Response<Tokens> socialLogin(@RequestBody String email) {
        return Response.success(oAuthService.socialLogin(email));
    }
}
