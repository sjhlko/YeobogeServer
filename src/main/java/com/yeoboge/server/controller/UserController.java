package com.yeoboge.server.controller;

import com.yeoboge.server.domain.dto.user.UserDetailResponse;
import com.yeoboge.server.domain.vo.response.Response;
import com.yeoboge.server.domain.vo.user.ProfileImgResponse;
import com.yeoboge.server.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/my")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @GetMapping("")
    public Response<UserDetailResponse> getProfile(Authentication authentication) {
        return Response.success(userService.getProfile(Long.parseLong(authentication.getName())));
    }

    @PatchMapping ("/profile-img")
    public Response<ProfileImgResponse> uploadFile(@RequestPart("file")MultipartFile file, Authentication authentication) {
        ProfileImgResponse profileImgResponse = userService.changeProfileImg(file,Long.parseLong(authentication.getName()));
        return Response.success(profileImgResponse);
    }

}
