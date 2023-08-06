package com.yeoboge.server.repository;

import com.yeoboge.server.domain.entity.ThemeOfBoardGame;
import com.yeoboge.server.domain.entity.id.ThemeOfBoardGamePK;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * {@link ThemeOfBoardGame} 관련 DB 쿼리에 대한 메서드를 제공하는 인터페이스
 */
@Repository
public interface ThemeOfBoardGameRepository extends JpaRepository<ThemeOfBoardGame, ThemeOfBoardGamePK> {
}
