package com.yeoboge.server.service.impl;

import com.yeoboge.server.domain.dto.boardGame.BoardGameThumbnailDto;
import com.yeoboge.server.domain.dto.recommend.RecommendForSingleResponse;
import com.yeoboge.server.domain.entity.BoardGame;
import com.yeoboge.server.domain.vo.recommend.RecommendIds;
import com.yeoboge.server.domain.vo.recommend.RecommendWebClientResponse;
import com.yeoboge.server.enums.error.CommonErrorCode;
import com.yeoboge.server.handler.AppException;
import com.yeoboge.server.repository.BoardGameRepository;
import com.yeoboge.server.service.RecommenderService;
import com.yeoboge.server.utils.WebClientUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.CountDownLatch;

/**
 * {@link RecommenderService} 구현체
 */
@Service
@RequiredArgsConstructor
public class RecommenderServiceImpl implements RecommenderService {
    private final BoardGameRepository boardGameRepository;
    private final WebClient webClient;

    @Override
    public RecommendForSingleResponse getSingleRecommendation(Long userId) {
        RecommendForSingleResponse response = new RecommendForSingleResponse(new HashMap<>());
        CountDownLatch latch = new CountDownLatch(1);

//        List<BoardGameThumbnailDto> recommends = getRecommendationFromML(userId, latch);
//        response.shelves().put("recommends", recommends);

        return response;
    }

    private List<BoardGameThumbnailDto> getRecommendationFromML(Long userId, CountDownLatch latch) {
        final String endPoint = "/recommends/{id}";
        Mono<RecommendWebClientResponse> mono = WebClientUtils.get(
                webClient, RecommendWebClientResponse.class, endPoint, userId
        );

        List<BoardGameThumbnailDto> recommends = new ArrayList<>();
        mono.subscribe(wr -> {
            getBoardGameData(wr.result(), recommends);
            latch.countDown();
        });

        try {
            latch.await();
        } catch (InterruptedException e) {
            throw new AppException(CommonErrorCode.INTERNAL_SERVER_ERROR);
        }

        return recommends;
    }

    /**
     * 추천 API가 생성한 추천 보드게임 ID를 토대로 각 보드게임의 썸네일을 조회한 뒤 리스트에 추가함.
     *
     * @param recommendIds 추천된 보드게임 ID 목록
     * @param thumbnailList 조회한 {@link BoardGameThumbnailDto}를 담을 {@link List}
     */
    private void getBoardGameData(List<RecommendIds> recommendIds, List<BoardGameThumbnailDto> thumbnailList) {
        for (RecommendIds id : recommendIds) {
            BoardGame boardGame = boardGameRepository.getById(id.id());
            thumbnailList.add(BoardGameThumbnailDto.of(boardGame));
        }
    }
}
