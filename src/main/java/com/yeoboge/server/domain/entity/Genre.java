package com.yeoboge.server.domain.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Getter
@Builder
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "genre")
public class Genre {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "genre_id")
    private Long id;
    private String name;
    @OneToMany(mappedBy = "genre", cascade = CascadeType.REMOVE, orphanRemoval = true)
    List<GenreOfBoardGame> boardGame  = new ArrayList<>();
}
