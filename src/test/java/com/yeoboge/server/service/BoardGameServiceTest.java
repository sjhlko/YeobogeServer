package com.yeoboge.server.service;

import com.yeoboge.server.domain.entity.BoardGame;
import com.yeoboge.server.domain.entity.User;
import com.yeoboge.server.domain.vo.response.MessageResponse;
import com.yeoboge.server.repository.BoardGameRepository;
import com.yeoboge.server.repository.BookmarkRepository;
import com.yeoboge.server.repository.RatingRepository;
import com.yeoboge.server.repository.UserRepository;
import com.yeoboge.server.service.impl.BoardGameServiceImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.DuplicateKeyException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class BoardGameServiceTest {
    @InjectMocks
    private BoardGameServiceImpl boardGameService;

    @Mock
    private BoardGameRepository boardGameRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private BookmarkRepository bookmarkRepository;
    @Mock
    private RatingRepository ratingRepository;

    @Test
    @DisplayName("보드게임 북마크 성공")
    public void addBookmarkSuccess() {
        // given
        BoardGame boardGame = BoardGame.builder().id(1L).build();
        User user = User.builder().id(1L).build();
        MessageResponse expected = MessageResponse
                .builder()
                .message("찜하기가 저장되었습니다")
                .build();

        // when
        when(boardGameRepository.getById(1L)).thenReturn(boardGame);
        when(userRepository.getById(1L)).thenReturn(user);

        MessageResponse actual = boardGameService.addBookmark(boardGame.getId(), user.getId());

        // then
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    @DisplayName("보드게임 북마크 실패: 이미 북마크한 보드게임 재요청")
    public void alreadyBookmarkedFail() {
        // given
        BoardGame boardGame = BoardGame.builder().id(1L).build();
        User user = User.builder().id(1L).build();

        // when
        when(bookmarkRepository.save(any())).thenThrow(DuplicateKeyException.class);

        // then
        assertThatThrownBy(() -> boardGameService.addBookmark(boardGame.getId(), user.getId()))
                .isInstanceOf(DataIntegrityViolationException.class);
    }
}
