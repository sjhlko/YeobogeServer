package com.yeoboge.server.service;

import com.yeoboge.server.domain.dto.boardGame.BoardGameDetailedThumbnailDto;
import com.yeoboge.server.domain.dto.recommend.GroupMembersResponse;
import com.yeoboge.server.domain.dto.recommend.GroupRecommendationResponse;
import com.yeoboge.server.domain.dto.recommend.UserGpsDto;
import com.yeoboge.server.domain.dto.user.UserInfoDto;
import com.yeoboge.server.domain.entity.Genre;
import com.yeoboge.server.domain.entity.User;
import com.yeoboge.server.domain.vo.recommend.GroupRecommendationRequest;
import com.yeoboge.server.domain.vo.recommend.RecommendWebClientResponse;
import com.yeoboge.server.enums.error.GroupErrorCode;
import com.yeoboge.server.handler.AppException;
import com.yeoboge.server.repository.*;
import com.yeoboge.server.service.impl.GroupRecommenderServiceImpl;
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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class GroupRecommenderServiceTest {
    @InjectMocks
    private GroupRecommenderServiceImpl groupRecommenderService;

    @Mock
    private UserRepository userRepository;
    @Mock
    private RatingRepository ratingRepository;
    @Mock
    private FriendRepository friendRepository;
    @Mock
    private RecommendationHistoryRepository historyRepository;
    @Mock
    private RecommendRepository recommendRepository;
    @Mock
    private TokenRepository tokenRepository;
    @Mock
    private PushAlarmService pushAlarmService;
    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private WebClient webClient;

    private UserGpsDto gpsDto;
    private GroupRecommendationRequest request;
    private GroupRecommendationResponse recommendationResponse;
    private List<BoardGameDetailedThumbnailDto> thumbnails;
    private List<Long> recommendableMembers;

    @Test
    @DisplayName("그룹 매칭 성공")
    public void groupMatchSuccess() throws InterruptedException {
        // given
        List<Long> groupIds = new ArrayList<>(List.of(1L, 2L, 3L));
        UserInfoDto userDto = new UserInfoDto(1L, "nickname", "image");
        GroupMembersResponse response = GroupMembersResponse.builder()
                .group(new ArrayList<>(Collections.nCopies(3, userDto)))
                .build();
        setUpGroupMatchTest();

        int numRequest = 3;
        ExecutorService executorService = Executors.newFixedThreadPool(numRequest);
        CountDownLatch latch = new CountDownLatch(numRequest);

        // when
        User user = User.builder().id(1L).nickname("nickname").profileImagePath("image").build();
        when(userRepository.getById(anyLong())).thenReturn(user);
        for (long userId : groupIds) {
            List<Long> friendIds = new ArrayList<>(groupIds);
            friendIds.remove(userId);
            when(friendRepository.findFriendIdsInIdList(eq(userId), anyList())).thenReturn(friendIds);
        }

        // then
        for (long userId : groupIds) {
            executorService.execute(() -> {
                GroupMembersResponse actual = groupRecommenderService.getGroupMembers(userId, gpsDto);
                assertThat(actual.getGroup()).isEqualTo(response.getGroup());
                assertThat(actual.getSeed()).isEqualTo(response.getSeed());
                latch.countDown();
            });
        }
        latch.await();
    }

    @Test
    @DisplayName("그룹 매칭 실패 : 최소 매칭 인원 수 미달")
    public void groupMatchFailByLessNumMember() {
        // given
        long userId = 1L;
        setUpGroupMatchTest();

        // when
        when(friendRepository.findFriendIdsInIdList(anyLong(), anyList())).thenReturn(Collections.EMPTY_LIST);

        // then
        assertThatThrownBy(() -> groupRecommenderService.getGroupMembers(userId, gpsDto))
                .isInstanceOf(AppException.class)
                .hasMessageContaining(GroupErrorCode.NO_GROUP_MEMBER.getMessage());
    }

    @Test
    @DisplayName("그룹 매칭 실패 : 최대 매칭 인원 수 초과")
    public void groupMatchFailByOverNumMember() {
        // given
        long userId = 1L;
        setUpGroupMatchTest();

        // when
        long memberId = 2L;
        List<Long> groupIds = new ArrayList<>(Collections.nCopies(11, memberId));
        when(friendRepository.findFriendIdsInIdList(anyLong(), anyList())).thenReturn(groupIds);

        // then
        assertThatThrownBy(() -> groupRecommenderService.getGroupMembers(userId, gpsDto))
                .isInstanceOf(AppException.class)
                .hasMessageContaining(GroupErrorCode.OVER_LIMIT_GROUP_MEMBER.getMessage());
    }

    @Test
    @DisplayName("그룹 추천 성공 : AI 추천")
    public void groupRecommendationSuccessByAI() {
        // given
        long userId = 1L;
        setUpGroupRecommendationTest();
        List<Long> recommendedByAi = new ArrayList<>(Collections.nCopies(10, 1L));
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
        when(ratingRepository.countByUser(anyLong())).thenReturn(15L);
        when(tokenRepository.findFcmToken(anyLong())).thenReturn(Optional.of("token"));
        when(recommendRepository.getRecommendedBoardGamesForGroup(recommendedByAi)).thenReturn(thumbnails);

        // then
        GroupRecommendationResponse actual = groupRecommenderService.getGroupRecommendation(userId, request);
        assertThat(actual.recommendations()).isEqualTo(recommendationResponse.recommendations());
        verify(recommendRepository, never()).getPopularBoardGamesOfGenreForGroup(anyLong());
    }

    @Test
    @DisplayName("그룹 추천 성공 : 단순 장르 인기순 추천")
    public void groupRecommendationSuccessByDB() {
        // given
        long userId = 1L;
        setUpGroupRecommendationTest();
        List<Genre> favoriteGenres = List.of(
                Genre.builder().id(1L).name("Strategy").build(),
                Genre.builder().id(2L).name("Abstract").build(),
                Genre.builder().id(3L).name("Party").build()
        );

        // when
        when(ratingRepository.countByUser(anyLong())).thenReturn(0L);
        when(recommendRepository.getMyFavoriteGenre(anyLong())).thenReturn(favoriteGenres);
        when(recommendRepository.getPopularBoardGamesOfGenreForGroup(anyLong())).thenReturn(thumbnails);

        // then
        GroupRecommendationResponse actual = groupRecommenderService.getGroupRecommendation(userId, request);
        assertThat(actual.recommendations()).isEqualTo(recommendationResponse.recommendations());
        verify(recommendRepository, never()).getRecommendedBoardGamesForGroup(anyList());
    }

    private void setUpGroupMatchTest() {
        gpsDto = new UserGpsDto(37.60, 127.09);
    }

    private void setUpGroupRecommendationTest() {
        recommendableMembers = new ArrayList<>(List.of(1L, 2L, 3L));
        request = new GroupRecommendationRequest(recommendableMembers, 1);

        BoardGameDetailedThumbnailDto dto = new BoardGameDetailedThumbnailDto(
                1L, "name", "image", 2, 5, "1", List.of("genre")
        );
        thumbnails = new ArrayList<>(Collections.nCopies(10, dto));
        recommendationResponse = new GroupRecommendationResponse(thumbnails);
    }
}
