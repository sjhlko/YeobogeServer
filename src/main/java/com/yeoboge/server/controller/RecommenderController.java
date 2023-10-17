package com.yeoboge.server.controller;

import com.yeoboge.server.domain.dto.recommend.*;
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
     * @return 맞춤 추천 목록을 포함해 카테고리 별 보드게임 썸네일 목록이 매핑된 {@link IndividualRecommendationResponse}
     */
    @GetMapping("")
    ResponseEntity<Response<IndividualRecommendationResponse>> getRecommendationForMe(
            @AuthenticationPrincipal Long userId
    ) {
        final int cacheMaxAge = 4 * 60;
        IndividualRecommendationResponse response = recommenderService.getSingleRecommendation(userId);

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

    /**
     * 그룹 구성원들의 취향에 따라 보드게임 목록을 추천하는 API
     *
     * @param request 추천을 받을 그룹 구성원에 관한 정보를 담은 {@link GroupRecommendationRequest}
     * @param userId 추천을 요청한 사용자 ID
     * @return 추천된 보드게임 목록의 상세 썸네일 정보를 포함한 {@link GroupRecommendationResponse}
     */
    @PostMapping("/group")
    Response<GroupRecommendationResponse> getGroupRecommendation(
            @RequestBody GroupRecommendationRequest request,
            @AuthenticationPrincipal long userId
    ) {
        GroupRecommendationResponse response = groupRecommenderService.getGroupRecommendation(userId, request);
        return Response.success(response);
    }
}
