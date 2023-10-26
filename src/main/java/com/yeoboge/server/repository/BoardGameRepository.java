package com.yeoboge.server.repository;

import com.yeoboge.server.domain.entity.BoardGame;
import com.yeoboge.server.enums.error.BoardGameErrorCode;
import com.yeoboge.server.handler.AppException;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * {@link BoardGame} 관련 DB 쿼리에 대한 메서드를 제공하는 인터페이스
 */
@Repository
public interface BoardGameRepository extends JpaRepository<BoardGame, Long>, CustomBoardGameRepository {

    /**
     * 특정 id의 보드게임의 정보에 대한 {@link BoardGame} 엔티티를 반환함.
     *
     * @param id 보드게임의 id
     * @return {@link BoardGame}
     * @throws AppException 해당 보드게임이 존재하지 않을 때 {@link BoardGameErrorCode} 의 존재하지 않음 에러를 던짐
     */
    default BoardGame getById(Long id) {
        return findById(id).orElseThrow(() -> new AppException(BoardGameErrorCode.BOARD_GAME_NOT_FOUND));
    }
}
