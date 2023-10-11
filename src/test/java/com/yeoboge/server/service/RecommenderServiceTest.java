package com.yeoboge.server.service;

import com.yeoboge.server.domain.dto.boardGame.BoardGameThumbnailDto;
import com.yeoboge.server.domain.dto.recommend.IndividualRecommendationResponse;
import com.yeoboge.server.domain.entity.Genre;
import com.yeoboge.server.domain.vo.recommend.RecommendWebClientResponse;
import com.yeoboge.server.enums.RecommendTypes;
import com.yeoboge.server.repository.RatingRepository;
import com.yeoboge.server.repository.RecommendRepository;
import com.yeoboge.server.service.impl.RecommenderServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Answers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.*;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class RecommenderServiceTest {
    @InjectMocks
    private RecommenderServiceImpl recommenderService;

    @Mock
    private RecommendRepository recommendRepository;
    @Mock
    private RatingRepository ratingRepository;
    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private WebClient webClient;

    private long userId;
    private List<Genre> favoriteGenres;
    private List<BoardGameThumbnailDto> thumbnails;
    private IndividualRecommendationResponse response;

    @BeforeEach
    public void setUp() {
        userId = 1L;
        favoriteGenres = List.of(
                Genre.builder().id(1L).name("Strategy").build(),
                Genre.builder().id(2L).name("Abstract").build(),
                Genre.builder().id(3L).name("Party").build()
        );
        thumbnails = List.of(
                new BoardGameThumbnailDto(1L, "board_game1", "image_path1"),
                new BoardGameThumbnailDto(2L, "board_game2", "image_path2"),
                new BoardGameThumbnailDto(3L, "board_game3", "image_path3"),
                new BoardGameThumbnailDto(4L, "board_game4", "image_path4"),
                new BoardGameThumbnailDto(5L, "board_game5", "image_path5"),
                new BoardGameThumbnailDto(6L, "board_game6", "image_path6"),
                new BoardGameThumbnailDto(7L, "board_game7", "image_path7"),
                new BoardGameThumbnailDto(8L, "board_game8", "image_path8"),
                new BoardGameThumbnailDto(9L, "board_game9", "image_path9"),
                new BoardGameThumbnailDto(10L, "board_game10", "image_path10")
        );
        response = new IndividualRecommendationResponse(
                new LinkedList<>(),
                new HashMap<>(),
                new HashMap<>()
        );
    }


    @Test
    @DisplayName("개인 추천 성공: 초기 사용자")
    public void recommendSuccessWithBaseUser() {
        // given
        List<String> keys = List.of(
                RecommendTypes.TOP_10.getKey(),
                RecommendTypes.FAVORITE_GENRE.getKey() + "Strategy",
                RecommendTypes.FAVORITE_GENRE.getKey() + "Abstract",
                RecommendTypes.FAVORITE_GENRE.getKey() + "Party"
        );
        setUpResponse(keys);

        // when
        mockSqlRepositories(0, false);
        IndividualRecommendationResponse actual = recommenderService.getSingleRecommendation(userId);

        // then
        assertResponse(actual);
        verify(recommendRepository, never()).getRecommendedBoardGames(anyList());
    }

    @Test
    @DisplayName("개인 추천 성공: AI 추천 제외")
    public void recommendSuccessWithoutAI() {
        // given
        List<String> keys = List.of(
                RecommendTypes.MY_BOOKMARK.getKey(),
                RecommendTypes.TOP_10.getKey(),
                RecommendTypes.FRIENDS_FAVORITE.getKey(),
                RecommendTypes.FAVORITE_GENRE.getKey() + "Strategy",
                RecommendTypes.FAVORITE_GENRE.getKey() + "Abstract",
                RecommendTypes.FAVORITE_GENRE.getKey() + "Party"
        );
        setUpResponse(keys);

        // when
        mockSqlRepositories(0, true);
        IndividualRecommendationResponse actual = recommenderService.getSingleRecommendation(userId);

        // then
        assertResponse(actual);
        verify(recommendRepository, never()).getRecommendedBoardGames(anyList());
    }

    @Test
    @DisplayName("개인 추천 성공: AI 추천 로직 포함")
    public void recommendSuccessWithAI() {
        // given
        List<String> keys = List.of(
                RecommendTypes.MY_BOOKMARK.getKey(),
                RecommendTypes.TOP_10.getKey(),
                RecommendTypes.FRIENDS_FAVORITE.getKey(),
                RecommendTypes.FAVORITE_GENRE.getKey() + "Strategy",
                RecommendTypes.FAVORITE_GENRE.getKey() + "Abstract",
                RecommendTypes.FAVORITE_GENRE.getKey() + "Party"
        );
        setUpResponse(keys);
        List<Long> recommendedByAi = List.of(1L, 2L, 3L, 4L, 5L, 6L, 7L, 8L, 9L, 10L);
        Mono<RecommendWebClientResponse> monoResponse = Mono.just(
                new RecommendWebClientResponse(recommendedByAi)
        );

        // when
        when(webClient.method(HttpMethod.POST)
                .uri(anyString())
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(any())
                .retrieve()
                .onStatus(any(), any())
                .onStatus(any(), any())
                .bodyToMono(RecommendWebClientResponse.class)
        ).thenReturn(monoResponse);
        mockSqlRepositories(10, true);
        IndividualRecommendationResponse actual = recommenderService.getSingleRecommendation(userId);

        // then
        assertResponse(actual);
        verify(recommendRepository, times(3))
                .getRecommendedBoardGames(recommendedByAi);
    }

    private void mockSqlRepositories(long numRating, boolean hasData) {
        List<BoardGameThumbnailDto> listByNumRating = hasData ? thumbnails : Collections.emptyList();

        when(recommendRepository.getMyFavoriteGenre(userId)).thenReturn(favoriteGenres);
        when(ratingRepository.countByUser(userId)).thenReturn(numRating);
        when(recommendRepository.getTopTenBoardGames()).thenReturn(thumbnails);
        when(recommendRepository.getFavoriteBoardGamesOfFriends(userId)).thenReturn(listByNumRating);
        when(recommendRepository.getMyBookmarkedBoardGames(userId)).thenReturn(listByNumRating);
        if (numRating < 10)
            when(recommendRepository.getPopularBoardGamesOfFavoriteGenre(any())).thenReturn(thumbnails);
        else
            when(recommendRepository.getRecommendedBoardGames(anyList())).thenReturn(thumbnails);
    }

    private void assertResponse(IndividualRecommendationResponse actual) {
        assertThat(actual.keys().size()).isEqualTo(response.keys().size());
        for (String key : response.keys()) {
            assertThat(actual.shelves().get(key)).isEqualTo(thumbnails);
            assertThat(actual.descriptions().get(key)).isEqualTo(response.descriptions().get(key));
        }
    }

    private List<String> setDescriptionByKey(List<String> keys) {
        List<String> descriptions = new ArrayList<>();

        for (String key : keys) {
            String description = "";
            if (key == "top10")
                description = "현재 인기 보드게임 🌟";
            else if (key == "bookmark")
                description = "내가 찜한 보드게임 🔖";
            else if (key == "friendsFavorite")
                description = "친구들이 좋아하는 보드게임 👥";
            else
                description = "내가 좋아하는 " + key.split("favoriteGenre")[1] + " 보드게임 🎲";

            descriptions.add(description);
        }

        return descriptions;
    }

    private void setUpResponse(List<String> keys) {
        List<String> descriptions = setDescriptionByKey(keys);
        response.keys().addAll(keys);
        for (int i = 0; i < keys.size(); i++) {
            response.shelves().put(keys.get(i), thumbnails);
            response.descriptions().put(keys.get(i), descriptions.get(i));
        }
    }
}
