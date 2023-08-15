package com.yeoboge.server.service;

import com.yeoboge.server.domain.dto.PageResponse;
import com.yeoboge.server.domain.dto.boardGame.BoardGameThumbnailDto;
import com.yeoboge.server.domain.dto.boardGame.TotalRatingsResponse;
import com.yeoboge.server.domain.dto.user.UserDetailResponse;
import com.yeoboge.server.domain.dto.user.UserUpdateRequest;
import com.yeoboge.server.domain.entity.User;
import com.yeoboge.server.domain.vo.MyBoardGamePageRequest;
import com.yeoboge.server.domain.vo.response.MessageResponse;
import com.yeoboge.server.enums.BoardGameOrderColumn;
import com.yeoboge.server.enums.error.AuthenticationErrorCode;
import com.yeoboge.server.enums.error.UserErrorCode;
import com.yeoboge.server.handler.AppException;
import com.yeoboge.server.repository.BookmarkRepository;
import com.yeoboge.server.repository.RatingRepository;
import com.yeoboge.server.repository.UserRepository;
import com.yeoboge.server.service.impl.UserServiceImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.mock.web.MockMultipartFile;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {
    @InjectMocks
    private UserServiceImpl userService;
    @Mock
    private UserRepository userRepository;
    @Mock
    private BookmarkRepository bookmarkRepository;
    @Mock
    private RatingRepository ratingRepository;
    @Mock
    private S3FileUploadService s3FileUploadService;

    @Test
    @DisplayName("회원 프로필 조회_성공")
    void getProfileSuccess() {
        // given
        User user = User.builder()
                .id(0L)
                .build();

        // when
        when(userRepository.getById(user.getId())).thenReturn(user);

        // then
        assertThat(userService.getProfile(user.getId())).isInstanceOf(UserDetailResponse.class);
    }

    @Test
    @DisplayName("회원 프로필 조회_실패 : 회원이 존재하지 않음")
    void getProfileFailed() {
        // given
        User user = User.builder()
                .id(0L)
                .build();

        // when
        when(userRepository.getById(user.getId())).thenThrow(new AppException(AuthenticationErrorCode.USER_NOT_FOUND));

        // then
        assertThatThrownBy(() -> userService.getProfile(user.getId()))
                .isInstanceOf(AppException.class)
                .hasMessageContaining(AuthenticationErrorCode.USER_NOT_FOUND.getMessage());
    }


    @Test
    @DisplayName("회원 프로필 변경_성공 : 프로필 사진 변경")
    void updateUserSuccess1() {
        // given
        User user = User.builder()
                .id(0L)
                .build();
        UserUpdateRequest request = UserUpdateRequest.builder()
                .isChanged(true)
                .nickname("변경")
                .build();
        MockMultipartFile file = new MockMultipartFile("file",
                "test.img", "png",
                "test file".getBytes(StandardCharsets.UTF_8) );
        String imgUrl = "url";

        // when
        when(userRepository.getById(user.getId())).thenReturn(user);
        when(s3FileUploadService.uploadFile(any())).thenReturn(imgUrl);
        when(userRepository.save(any())).thenReturn(user);

        // then
        assertThat(userService.updateUser(file, request, user.getId()))
                .isEqualTo(MessageResponse.builder()
                        .message("프로필 변경 성공")
                        .build());
    }

    @Test
    @DisplayName("회원 프로필 변경_성공 : 프로필 사진 유지")
    void updateUserSuccess2() {
        // given
        User user = User.builder()
                .id(0L)
                .build();
        UserUpdateRequest request = UserUpdateRequest.builder()
                .isChanged(false)
                .nickname("변경")
                .build();

        // when
        when(userRepository.getById(user.getId())).thenReturn(user);
        when(userRepository.save(any())).thenReturn(user);

        // then
        assertThat(userService.updateUser(null, request, user.getId()))
                .isEqualTo(MessageResponse.builder()
                        .message("프로필 변경 성공")
                        .build());
    }

    @Test
    @DisplayName("회원 프로필 변경_성공 : 프로필 사진 삭제")
    void updateUserSuccess3() {
        // given
        User user = User.builder()
                .id(0L)
                .build();
        UserUpdateRequest request = UserUpdateRequest.builder()
                .isChanged(true)
                .nickname("변경")
                .build();

        // when
        when(userRepository.getById(user.getId())).thenReturn(user);
        when(userRepository.save(any())).thenReturn(user);

        // then
        assertThat(userService.updateUser(null, request, user.getId()))
                .isEqualTo(MessageResponse.builder()
                        .message("프로필 변경 성공")
                        .build());
    }

    @Test
    @DisplayName("회원 프로필 변경_실패 : 회원이 존재하지 않음")
    void updateUserFailed1() {
        // given
        User user = User.builder()
                .id(0L)
                .build();
        UserUpdateRequest request = UserUpdateRequest.builder()
                .isChanged(true)
                .nickname("변경")
                .build();
        MockMultipartFile file = new MockMultipartFile("file",
                "test.img", "png",
                "test file".getBytes(StandardCharsets.UTF_8) );

        // when
        when(userRepository.getById(user.getId()))
                .thenThrow(new AppException(AuthenticationErrorCode.USER_NOT_FOUND));

        // then
        assertThatThrownBy(() -> userService.updateUser(file, request, user.getId()))
                .isInstanceOf(AppException.class)
                .hasMessageContaining(AuthenticationErrorCode.USER_NOT_FOUND.getMessage());
    }

    @Test
    @DisplayName("회원 프로필 변경_실패 : 프로필 사진 업로드 실패")
    void updateUserFailed2() {
        // given
        User user = User.builder()
                .id(0L)
                .build();
        UserUpdateRequest request = UserUpdateRequest.builder()
                .isChanged(true)
                .nickname("변경")
                .build();
        MockMultipartFile file = new MockMultipartFile("file",
                "test.img", "png",
                "test file".getBytes(StandardCharsets.UTF_8));

        // when
        when(userRepository.getById(user.getId())).thenReturn(user);
        when(s3FileUploadService.uploadFile(any())).thenThrow(
                new AppException(UserErrorCode.FILE_UPLOAD_ERROR)
        );

        // then
        assertThatThrownBy(() -> userService.updateUser(file, request, user.getId()))
                .isInstanceOf(AppException.class)
                .hasMessageContaining(UserErrorCode.FILE_UPLOAD_ERROR.getMessage());
    }

    @Test
    @DisplayName("찜한 보드게임 조회 성공: 첫 번째 페이지")
    public void getMyBookmarksFirstPageSuccess() {
        // given
        MyBoardGamePageRequest pageRequest = setPageRequest(0);
        Page page = getBoardGamePage(pageRequest.of(), 5);
        PageResponse expected = new PageResponse(page);

        // when
        when(bookmarkRepository.getBookmarkByUserId(1L, pageRequest.of()))
                .thenReturn(page);

        PageResponse actual = userService.getMyBookmarks(1L, pageRequest);

        // then
        assertThat(actual.getContent()).isEqualTo(expected.getContent());
        assertThat(actual.getPrevPage()).isNull();
        assertThat(actual.getNextPage()).isNotNull();
    }

    @Test
    @DisplayName("찜한 보드게임 조회 성공: 중간 페이지")
    public void getMyBookmarksMiddlePageSuccess() {
        // given
        MyBoardGamePageRequest pageRequest = setPageRequest(1);
        Page page = getBoardGamePage(pageRequest.of(), 5);
        PageResponse expected = new PageResponse(page);

        // when
        when(bookmarkRepository.getBookmarkByUserId(1L, pageRequest.of()))
                .thenReturn(page);

        PageResponse actual = userService.getMyBookmarks(1L, pageRequest);

        // then
        assertThat(actual.getContent()).isEqualTo(expected.getContent());
        assertThat(actual.getPrevPage()).isNotNull();
        assertThat(actual.getNextPage()).isNotNull();
    }

    @Test
    @DisplayName("찜한 보드게임 조회 성공: 마지막 페이지")
    public void getMyBookmarksLastPageSuccess() {
        // given
        MyBoardGamePageRequest pageRequest = setPageRequest(4);
        Page page = getBoardGamePage(pageRequest.of(), 5);
        PageResponse expected = new PageResponse(page);

        // when
        when(bookmarkRepository.getBookmarkByUserId(1L, pageRequest.of()))
                .thenReturn(page);

        PageResponse actual = userService.getMyBookmarks(1L, pageRequest);

        // then
        assertThat(actual.getContent()).isEqualTo(expected.getContent());
        assertThat(actual.getPrevPage()).isNotNull();
        assertThat(actual.getNextPage()).isNull();
    }

    @Test
    @DisplayName("평가한 보드게임 별점 별로 조회 성공: 평가한 보드게임 존재함")
    public void getMyAllRatingsSuccess() {
        // given
        Long userId = 1L;
        Double ratedScore = 3.5;
        List<Double> ratingGroup = List.of(ratedScore);
        List<BoardGameThumbnailDto> boardGames = getBoardGameList();

        // when
        when(ratingRepository.getUserRatingGroup(userId)).thenReturn(ratingGroup);
        when(ratingRepository.getRatingByUserId(userId, ratedScore)).thenReturn(boardGames);

        TotalRatingsResponse actual = (TotalRatingsResponse) userService.getMyAllRatings(userId);

        // then
        assertThat(actual.getBoardGames().get(ratedScore)).isEqualTo(boardGames);
    }

    @Test
    @DisplayName("평가한 보드게임 별점 별로 조회 성공: 평가한 보드게임 없음")
    public void getMyEmptyRatingsSuccess() {
        // given
        Long userId = 1L;
        List<Double> ratingGroup = List.of();

        // when
        when(ratingRepository.getUserRatingGroup(userId)).thenReturn(ratingGroup);

        TotalRatingsResponse actual = (TotalRatingsResponse) userService.getMyAllRatings(userId);

        // then
        assertThat(actual.getBoardGames()).isEmpty();
        verify(ratingRepository, never()).getRatingByUserId(any(), any());
    }

    @Test
    @DisplayName("특정 별점으로 평가한 보드게임 조회 성공: 첫 번째 페이지")
    public void getMyRatingByScoreFirstPageSuccess() {
        // given
        Long userId = 1L;
        Double score = 3.5;
        MyBoardGamePageRequest pageRequest = setPageRequest(0);
        Page page = getBoardGamePage(pageRequest.of(), 5);
        PageResponse expected = new PageResponse(page);

        // when
        when(ratingRepository.getRatingsByUserId(userId, score, pageRequest.of())).thenReturn(page);

        PageResponse actual = userService.getMyRatingsByScore(userId, score, pageRequest);

        // then
        assertThat(actual.getContent()).isEqualTo(expected.getContent());
        assertThat(actual.getPrevPage()).isNull();
        assertThat(actual.getNextPage()).isNotNull();
    }

    @Test
    @DisplayName("특정 별점으로 평가한 보드게임 조회 성공: 중간 페이지")
    public void getMyRatingsByScoreMiddlePageSuccess() {
        // given
        Long userId = 1L;
        Double score = 3.5;
        MyBoardGamePageRequest pageRequest = setPageRequest(1);
        Page page = getBoardGamePage(pageRequest.of(), 5);
        PageResponse expected = new PageResponse(page);

        // when
        when(ratingRepository.getRatingsByUserId(userId, score, pageRequest.of())).thenReturn(page);

        PageResponse actual = userService.getMyRatingsByScore(userId, score, pageRequest);

        // then
        assertThat(actual.getContent()).isEqualTo(expected.getContent());
        assertThat(actual.getPrevPage()).isNotNull();
        assertThat(actual.getNextPage()).isNotNull();
    }

    @Test
    @DisplayName("특정 별점으로 평가한 보드게임 조회 성공: 마지막 페이지")
    public void getMyRatingsByScoreLasePageSuccess() {
        // given
        Long userId = 1L;
        Double score = 3.5;
        MyBoardGamePageRequest pageRequest = setPageRequest(4);
        Page page = getBoardGamePage(pageRequest.of(), 5);
        PageResponse expected = new PageResponse(page);

        // when
        when(ratingRepository.getRatingsByUserId(userId, score, pageRequest.of())).thenReturn(page);

        PageResponse actual = userService.getMyRatingsByScore(userId, score, pageRequest);

        // then
        assertThat(actual.getContent()).isEqualTo(expected.getContent());
        assertThat(actual.getPrevPage()).isNotNull();
        assertThat(actual.getNextPage()).isNull();
    }

    private MyBoardGamePageRequest setPageRequest(int page) {
        MyBoardGamePageRequest pageRequest = new MyBoardGamePageRequest();
        pageRequest.setPage(page);
        pageRequest.setSize(1);
        pageRequest.setSort(BoardGameOrderColumn.NEW);

        return pageRequest;
    }

    private Page<BoardGameThumbnailDto> getBoardGamePage(Pageable pageable, int total) {
        List<BoardGameThumbnailDto> contents = getBoardGameList();
        return new PageImpl(contents, pageable, total);
    }

    private List<BoardGameThumbnailDto> getBoardGameList() {
        BoardGameThumbnailDto dto = new BoardGameThumbnailDto(
                1L,
                "board_game1",
                "image_path"
        );
        return List.of(dto);
    }
}