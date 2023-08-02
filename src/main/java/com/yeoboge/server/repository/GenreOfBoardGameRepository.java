package com.yeoboge.server.repository;

import com.yeoboge.server.domain.entity.GenreOfBoardGame;
import com.yeoboge.server.domain.entity.id.GenreOfBoardGamePK;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GenreOfBoardGameRepository extends JpaRepository<GenreOfBoardGame, GenreOfBoardGamePK> {
}
