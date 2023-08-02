package com.yeoboge.server.domain.entity.id;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GenreOfBoardGamePK implements Serializable {
    private Long genre;
    private Long boardGame;
}
