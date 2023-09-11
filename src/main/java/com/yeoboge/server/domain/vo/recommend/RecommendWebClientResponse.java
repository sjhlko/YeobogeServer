package com.yeoboge.server.domain.vo.recommend;

import java.util.List;

/**
 * 추천 목록 생성 외부 API의 응답 형식에 매핑할 VO
 * @param result
 */
public record RecommendWebClientResponse(List<RecommendIds> result) {
}
