package com.yeoboge.server.domain.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

@Builder
@Entity
@NoArgsConstructor
@AllArgsConstructor
public class BoardGame {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
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
    @Column(columnDefinition = "TEXT")
    private String mechanism;


}
