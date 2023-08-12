package com.yeoboge.server.repository;

import com.yeoboge.server.domain.entity.BoardGame;
import com.yeoboge.server.enums.error.BoardGameErrorCode;
import com.yeoboge.server.handler.AppException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * {@link BoardGame} 관련 DB 쿼리에 대한 메서드를 제공하는 인터페이스
 */
@Repository
public interface BoardGameRepository extends JpaRepository<BoardGame, Long>, BoardGameQueryDslRepository {
    default BoardGame getById(Long id) {
        return findById(id).orElseThrow(() -> new AppException(BoardGameErrorCode.BOARD_GAME_NOT_FOUND));
    }
    Page<BoardGame> findAllByPlayerMinGreaterThanAndNameContains(Pageable pageable, Integer player, String name);
}
