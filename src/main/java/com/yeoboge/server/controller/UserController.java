package com.yeoboge.server.controller;

import com.yeoboge.server.domain.dto.user.UserDetailResponse;
import com.yeoboge.server.domain.vo.response.Response;
import com.yeoboge.server.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/my")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    @GetMapping("")
    public Response<UserDetailResponse> getProfile(Authentication authentication) {
        return Response.success(userService.getProfile(Long.parseLong(authentication.getName())));
    }
}
