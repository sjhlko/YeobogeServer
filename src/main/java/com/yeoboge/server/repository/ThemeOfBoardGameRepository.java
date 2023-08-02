package com.yeoboge.server.repository;

import com.yeoboge.server.domain.entity.ThemeOfBoardGame;
import com.yeoboge.server.domain.entity.id.ThemeOfBoardGamePK;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ThemeOfBoardGameRepository extends JpaRepository<ThemeOfBoardGame, ThemeOfBoardGamePK> {
}
