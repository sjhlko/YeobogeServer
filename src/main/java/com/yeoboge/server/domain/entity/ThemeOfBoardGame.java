package com.yeoboge.server.domain.entity;

import com.yeoboge.server.domain.entity.id.ThemeOfBoardGamePK;
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
@IdClass(ThemeOfBoardGamePK.class)
public class ThemeOfBoardGame {
    @ManyToOne(fetch = FetchType.LAZY)
    @Id
    @JoinColumn(name = "theme_id", nullable = false)
    private Theme theme;
    @ManyToOne(fetch = FetchType.LAZY)
    @Id
    @JoinColumn(name = "board_game_id", nullable = false)
    private BoardGame boardGame;
}
