package com.yeoboge.server.domain.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BoardGame {
    @Id
    @Column(name = "board_game_id")
    private Long id;
    private String name;
    @Column(columnDefinition = "TEXT")
    private String description;
    private String weight;
    private Integer playerMin;
    private Integer playerMax;
    @Column(columnDefinition = "TEXT")
    private String imagePath;
    private Integer playTime;
    @Enumerated(EnumType.STRING)
    private IsLocalized isLocalized;
    @OneToMany(mappedBy = "boardGame", cascade = CascadeType.REMOVE, orphanRemoval = true)
    List<GenreOfBoardGame> genre  = new ArrayList<>();
    @OneToMany(mappedBy = "boardGame", cascade = CascadeType.REMOVE, orphanRemoval = true)
    List<ThemeOfBoardGame> theme  = new ArrayList<>();
    @OneToMany(mappedBy = "boardGame", cascade = CascadeType.REMOVE, orphanRemoval = true)
    List<MechanismOfBoardGame> mechanism  = new ArrayList<>();
}
