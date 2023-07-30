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

@RestController
@RequestMapping("/oauths")
@RequiredArgsConstructor
public class OAuthController {
    private final OAuthService oAuthService;

    @PostMapping("/register")
    public ResponseEntity<Response<Tokens>> socialRegister(@RequestBody SocialRegisterRequest request) {
        return Response.created(oAuthService.socialRegister(request));
    }

    @PostMapping("/login")
    public Response<Tokens> socialLogin(@RequestBody String email) {
        return Response.success(oAuthService.socialLogin(email));
    }
}
