package com.yeoboge.server.domain.entity.id;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ThemeOfBoardGamePK implements Serializable {
    private Long theme;
    private Long boardGame;
}
