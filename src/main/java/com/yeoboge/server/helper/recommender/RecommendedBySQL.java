package com.yeoboge.server.helper.recommender;

import com.yeoboge.server.domain.dto.boardGame.BoardGameThumbnailDto;
import com.yeoboge.server.domain.dto.recommend.RecommendForSingleResponse;
import com.yeoboge.server.enums.RecommendTypes;
import com.yeoboge.server.repository.RecommendRepository;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;

/**
 * 개인 추천 중 DB 조회와 관련된 로직들에 공통된 로직을 분리하기 위한 추상 클래스
 */
public abstract class RecommendedBySQL extends RecommendedBySomethingBase {
    protected CompletableFuture<List<BoardGameThumbnailDto>> future;

    RecommendedBySQL(RecommendRepository repository, RecommendTypes type) {
        super(repository, type);
    }

    /**
     * 외부 API를 요청하는 로직이 비동기로 작업하므로 DB에서 추천 목록을 조회하는 로직도 비동기 동작하도록 설정하고,
     * 비동기로 받은 조회 결과를 {@link RecommendForSingleResponse}에 추가함.
     *
     * @param response 추천 목록 관련 메타데이터를 담기 위한 {@link RecommendForSingleResponseㄹ직}
     * @param latch 비동기 로직 완료를 기다리기 위한 {@link CountDownLatch}
     */
    protected void setAsyncProcessing(RecommendForSingleResponse response, CountDownLatch latch) {
        future.thenCompose(
                boardGames -> CompletableFuture.runAsync(() -> addToResponse(response, boardGames))
        ).thenRun(() -> latch.countDown());
    }
}
