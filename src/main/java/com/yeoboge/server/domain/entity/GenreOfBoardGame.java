package com.yeoboge.server.domain.entity;

import com.yeoboge.server.domain.entity.id.GenreOfBoardGamePK;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@IdClass(GenreOfBoardGamePK.class)
public class GenreOfBoardGame {
    @ManyToOne(fetch = FetchType.LAZY)
    @Id
    @JoinColumn(name = "genre_id", nullable = false)
    private Genre genre;
    @ManyToOne(fetch = FetchType.LAZY)
    @Id
    @JoinColumn(name = "board_game_id", nullable = false)
    private BoardGame boardGame;
}
