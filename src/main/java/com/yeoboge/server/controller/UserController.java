package com.yeoboge.server.controller;

import com.yeoboge.server.domain.dto.user.UserDetailResponse;
import com.yeoboge.server.domain.dto.user.UserUpdateRequest;
import com.yeoboge.server.domain.vo.auth.UpdatePasswordRequest;
import com.yeoboge.server.domain.vo.response.MessageResponse;
import com.yeoboge.server.domain.vo.response.Response;
import com.yeoboge.server.service.UserService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

/**
 * 회원 관련 API 엔드포인트에 대해 매핑되는 Rest Controller
 *
 * @author Seo Jeonghee
 */
@RestController
@RequestMapping("/my")
@SecurityRequirement(name = "Bearer Authentication")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    /**
     * 회원 정보를 조회하는 API
     *
     * @param id 현재 로그인한 회원의 인덱스
     * @return 회원의 이메일, 닉네임, 유저 코드, 프로필 사진 링크에 대한 {@link UserDetailResponse} VO, HTTP 200 응답
     */
    @GetMapping("")
    public Response<UserDetailResponse> getProfile(@AuthenticationPrincipal Long id) {
        return Response.success(userService.getProfile(id));
    }

    /**
     * 회원의 프로필을 수정하는 API
     *
     * @param file 변경할 프로필 사진
     * @param request 프로필 사진이 변경되었는지 여부와, 바꾸고자 하는 닉네임에 대한 {@link UserUpdateRequest} VO
     * @param id 현재 로그인한 회원의 인덱스
     * @return 프로필 변경 성공됨 메세지를 포함한 HTTP 200 응답
     * @see MessageResponse
     */
    @PatchMapping ("")
    public Response<MessageResponse> updateUser(@RequestPart(value = "file", required = false)MultipartFile file,
                                                @RequestPart("data") UserUpdateRequest request,
                                                @AuthenticationPrincipal Long id) {
        MessageResponse response = userService.updateUser(file, request, id);
        return Response.success(response);
    }

}
