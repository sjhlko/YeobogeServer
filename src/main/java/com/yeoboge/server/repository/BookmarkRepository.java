package com.yeoboge.server.repository;

import com.yeoboge.server.domain.entity.BookmarkedBoardGame;
import com.yeoboge.server.enums.error.CommonErrorCode;
import com.yeoboge.server.handler.AppException;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * {@link BookmarkedBoardGame} 관련 DB 쿼리에 대한 메서드를 제공하는 인터페이스
 */
@Repository
public interface BookmarkRepository extends JpaRepository<BookmarkedBoardGame, Long>, CustomBookmarkRepository {
    /**
     * 회원이 특정 보드게임에 대해 찜한 정보를 조회함.
     *
     * @param userId 조회할 회원 ID
     * @param boardGameId 찜한 보드게임 ID
     * @return {@link BookmarkedBoardGame}의 {@link Optional} 객체
     */
    @Query("SELECT b FROM BookmarkedBoardGame b WHERE b.user.id = :userId and b.boardGame.id = :boardGameId")
    Optional<BookmarkedBoardGame> findByParentId(Long userId, Long boardGameId);

    /**
     * 회원이 찜한 보드게임에 대해 {@link BookmarkedBoardGame} 엔티티를 반환함.
     *
     * @param userId 회원 ID
     * @param boardGameId 찜한 보드게임 ID
     * @return {@link BookmarkedBoardGame}
     * @throws AppException 해당 회원이 보드게임을 찜하지 않았을 때, 404 응답인 {@code NOT_FOUND} 예외를 던짐.
     */
    default BookmarkedBoardGame getByParentId(Long userId, Long boardGameId) {
        return findByParentId(userId, boardGameId)
                .orElseThrow(() -> new AppException(CommonErrorCode.NOT_FOUND));
    }
}
