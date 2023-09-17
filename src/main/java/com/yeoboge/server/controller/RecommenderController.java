package com.yeoboge.server.controller;

import com.yeoboge.server.domain.dto.recommend.RecommendForSingleResponse;
import com.yeoboge.server.domain.vo.response.Response;
import com.yeoboge.server.service.RecommenderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 추천 관련 API 엔드포인트에 대해 매핑되는 Rest Controller
 */
@RestController
@RequestMapping("/recommends")
@RequiredArgsConstructor
public class RecommenderController {
    private final RecommenderService recommenderService;

    /**
     * 사용자 개인 맞춤 추천 및 카테고리 별 보드게임 목록을 조회하는 API
     *
     * @param userId 조회할 사용자 ID
     * @return 맞춤 추천 목록을 포함해 카테고리 별 보드게임 썸네일 목록이 매핑된 {@link RecommendForSingleResponse}
     */
    @GetMapping("")
    ResponseEntity<Response<RecommendForSingleResponse>> get(@AuthenticationPrincipal Long userId) {
        final int cacheMaxAge = 5 * 60;
        RecommendForSingleResponse response = recommenderService.getSingleRecommendation(userId);

        return Response.cached(response, cacheMaxAge);
    }
}
