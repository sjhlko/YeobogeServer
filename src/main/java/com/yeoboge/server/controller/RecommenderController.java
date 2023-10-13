package com.yeoboge.server.controller;

import com.yeoboge.server.domain.dto.recommend.GroupMembersResponse;
import com.yeoboge.server.domain.dto.recommend.RecommendationResponse;
import com.yeoboge.server.domain.dto.recommend.UserGpsDto;
import com.yeoboge.server.domain.vo.recommend.GroupRecommendationRequest;
import com.yeoboge.server.domain.vo.response.Response;
import com.yeoboge.server.service.GroupRecommenderService;
import com.yeoboge.server.service.RecommenderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

/**
 * 추천 관련 API 엔드포인트에 대해 매핑되는 Rest Controller
 */
@RestController
@RequestMapping("/recommends")
@RequiredArgsConstructor
public class RecommenderController {
    private final RecommenderService recommenderService;
    private final GroupRecommenderService groupRecommenderService;

    /**
     * 사용자 개인 맞춤 추천 및 카테고리 별 보드게임 목록을 조회하는 API
     *
     * @param userId 조회할 사용자 ID
     * @return 맞춤 추천 목록을 포함해 카테고리 별 보드게임 썸네일 목록이 매핑된 {@link RecommendationResponse}
     */
    @GetMapping("")
    ResponseEntity<Response<RecommendationResponse>> getRecommendationForMe(@AuthenticationPrincipal Long userId) {
        final int cacheMaxAge = 4 * 60;
        RecommendationResponse response = recommenderService.getSingleRecommendation(userId);

        return Response.cached(response, cacheMaxAge);
    }

    /**
     * 사용자와 현재 같은 위치인 친구와 그룹을 맺는 API
     *
     * @param userId 그룹 맺기를 요청한 사용자 ID
     * @param gpsDto 사용자의 현재 GPS 정보를 담은 {@link UserGpsDto}
     * @return 해당 그룹 구성원들의 기본 정보 리스트가 포함된 {@link GroupMembersResponse}
     */
    @PostMapping("/group-match")
    Response<GroupMembersResponse> getGroupMembers(
            @AuthenticationPrincipal long userId, @RequestBody UserGpsDto gpsDto
    ) {
        GroupMembersResponse response = groupRecommenderService.getGroupMembers(userId, gpsDto);
        return Response.success(response);
    }

    @PostMapping("/group")
    Response<RecommendationResponse> getGroupRecommendation(@RequestBody GroupRecommendationRequest request) {
        RecommendationResponse response = groupRecommenderService.getGroupRecommendation(request);
        return Response.success(response);
    }
}
