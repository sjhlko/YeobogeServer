package com.yeoboge.server.service.impl;

import com.yeoboge.server.domain.dto.boardGame.BoardGameThumbnailDto;
import com.yeoboge.server.domain.dto.recommend.RecommendForSingleResponse;
import com.yeoboge.server.domain.entity.BoardGame;
import com.yeoboge.server.domain.vo.recommend.RecommendIds;
import com.yeoboge.server.domain.vo.recommend.RecommendWebClientResponse;
import com.yeoboge.server.enums.RecommendTypes;
import com.yeoboge.server.enums.error.CommonErrorCode;
import com.yeoboge.server.handler.AppException;
import com.yeoboge.server.repository.BoardGameRepository;
import com.yeoboge.server.repository.GenreRepository;
import com.yeoboge.server.repository.RecommendRepository;
import com.yeoboge.server.repository.UserRepository;
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
    private final RecommendRepository recommendRepository;
    private final GenreRepository genreRepository;
    private final UserRepository userRepository;

    private final WebClient webClient;

    @Override
    public RecommendForSingleResponse getSingleRecommendation(Long userId) {
        RecommendForSingleResponse response = new RecommendForSingleResponse(
                new ArrayList<>(), new HashMap<>(), new HashMap<>()
        );
        CountDownLatch latch = new CountDownLatch(1);

//        List<BoardGameThumbnailDto> recommends = getRecommendationFromML(userId, latch);
//        response.shelves().put("recommends", recommends);

        Long favoriteGenreId = recommendRepository.getMyFavoriteGenreId(userId);
        String genre = genreRepository.getNameById(favoriteGenreId);
        String nickname = userRepository.getById(userId).getNickname();

        response.shelves().put(RecommendTypes.MY_BOOKMARK.getKey(),
                recommendRepository.getMyBookmarkedBoardGames(userId));
        response.shelves().put(RecommendTypes.TOP_10.getKey(),
                recommendRepository.getTopTenBoardGames());
        response.shelves().put(RecommendTypes.FAVORITE_GENRE.getKey(),
                recommendRepository.getPopularBoardGamesOfFavoriteGenre(userId, favoriteGenreId));
        response.shelves().put(RecommendTypes.FRIENDS_FAVORITE.getKey(),
                recommendRepository.getFavoriteBoardGamesOfFriends(userId));

        response.setMetadata(nickname, genre);

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
