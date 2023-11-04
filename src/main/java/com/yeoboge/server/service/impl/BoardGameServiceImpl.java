package com.yeoboge.server.service.impl;

import com.yeoboge.server.domain.dto.PageResponse;
import com.yeoboge.server.domain.dto.boardGame.*;
import com.yeoboge.server.domain.entity.*;
import com.yeoboge.server.domain.vo.boardgame.SearchBoardGameRequest;
import com.yeoboge.server.domain.vo.response.MessageResponse;
import com.yeoboge.server.repository.BoardGameRepository;
import com.yeoboge.server.repository.BookmarkRepository;
import com.yeoboge.server.repository.RatingRepository;
import com.yeoboge.server.repository.UserRepository;
import com.yeoboge.server.service.BoardGameService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

/**
 * {@link BoardGameService} 구현체
 */
@Service
@RequiredArgsConstructor
public class BoardGameServiceImpl implements BoardGameService {
    private final BoardGameRepository boardGameRepository;
    private final UserRepository userRepository;
    private final BookmarkRepository bookmarkRepository;
    private final RatingRepository ratingRepository;

    @Override
    public BoardGameDetailResponse getBoardGameDetail(long userId, long boardGameId) {
        BoardGameDetailDto detail = boardGameRepository.getBoardGameDetail(boardGameId);
        BookmarkRatingOfUserDto userDto = getUserBookmarkAndRating(userId, boardGameId);
        FriendReviewDto like = ratingRepository.getFriendReviewOfBoardGame(userId, boardGameId, true);
        FriendReviewDto dislike = ratingRepository.getFriendReviewOfBoardGame(userId, boardGameId, false);

        return BoardGameDetailResponse.builder()
                .boardGame(detail)
                .my(userDto)
                .like(like)
                .dislike(dislike)
                .build();
    }

    @Override
    public MessageResponse addBookmark(Long id, Long userId) {
        BoardGame boardGame = boardGameRepository.getById(id);
        User user = userRepository.getById(userId);

        BookmarkedBoardGame bookmark = new BookmarkedBoardGame();
        bookmark.setParent(user, boardGame);
        bookmarkRepository.save(bookmark);

        return MessageResponse.builder()
                .message("찜하기가 저장되었습니다")
                .build();
    }

    @Override
    public void removeBookmark(Long id, Long userId) {
        BookmarkedBoardGame bookmark = bookmarkRepository.getByParentId(userId, id);
        bookmarkRepository.delete(bookmark);
    }

    @Override
    public MessageResponse rateBoardGame(Long id, Long userId, RatingRequest request) {
        BoardGame boardGame = boardGameRepository.getById(id);
        User user = userRepository.getById(userId);
        Double score = request.score();

        if (score != 0) saveRating(user, boardGame, score);
        else deleteRating(userId, id);

        return MessageResponse.builder()
                .message("평가가 저장되었습니다.")
                .build();
    }

    @Override
    public PageResponse searchBoardGame(
            Pageable pageable,
            SearchBoardGameRequest request
    ) {
        Page<BoardGameDetailedThumbnailDto> searchResults = boardGameRepository
                .findBoardGameBySearchOption(pageable, request);
        PageResponse responses = new PageResponse(searchResults);
        return responses;
    }

    /**
     * 특정 보드게임에 대한 사용자의 평점 및 북마크 여부를 조회함.
     *
     * @param userId 사용자 ID
     * @param boardGameId 보드게임 ID
     * @return 사용자 평점 및 북마크 여부가 포함된 {@link BookmarkRatingOfUserDto}
     */
    private BookmarkRatingOfUserDto getUserBookmarkAndRating(long userId, long boardGameId) {
        Double userRating = ratingRepository.findByParentId(userId, boardGameId).orElse(new Rating()).getScore();
        boolean isBookmarked = bookmarkRepository.findByParentId(userId, boardGameId).isPresent();

        return BookmarkRatingOfUserDto.builder()
                .rating(userRating)
                .isBookmarked(isBookmarked)
                .build();
    }

    /**
     * 보드게임에 대해 평가를 저장함.
     *
     * @param user 평가를 남길 회원
     * @param boardGame 평가를 남길 보드게임
     * @param score 평가할 별점
     */
    private void saveRating(User user, BoardGame boardGame, Double score) {
        Rating rating = ratingRepository.getOrNewByParentId(user.getId(), boardGame.getId());
        rating.setParent(user, boardGame);
        rating.setScore(score);
        ratingRepository.save(rating);
    }

    /**
     * 보드게임에 대한 평가를 삭제함.
     *
     * @param userId 평가를 취소할 회원 ID
     * @param boardGameId 평가를 취소할 보드게임 ID
     */
    private void deleteRating(Long userId, Long boardGameId) {
        Rating rating = ratingRepository.getByParentId(userId, boardGameId);
        ratingRepository.delete(rating);
    }
}
