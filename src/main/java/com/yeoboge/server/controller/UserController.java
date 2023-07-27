package com.yeoboge.server.controller;

import com.yeoboge.server.domain.dto.user.UserDetailResponse;
import com.yeoboge.server.domain.dto.user.UserUpdateRequest;
import com.yeoboge.server.domain.vo.response.Response;
import com.yeoboge.server.domain.vo.user.UpdateUser;
import com.yeoboge.server.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/my")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @GetMapping("")
    public Response<UserDetailResponse> getProfile(@AuthenticationPrincipal Long id) {
        return Response.success(userService.getProfile(id));
    }

    @PatchMapping ("")
    public Response<UpdateUser> updateUser(@RequestPart("file")MultipartFile file, @RequestPart("data") UserUpdateRequest request, @AuthenticationPrincipal Long id) {
        UpdateUser profileImgResponse = userService.updateUser(file,request,id);
        return Response.success(profileImgResponse);
    }

}
