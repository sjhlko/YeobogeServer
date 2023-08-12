package com.yeoboge.server.domain.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Getter
@Builder
@Entity
@NoArgsConstructor
@AllArgsConstructor
public class Mechanism {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "mechanism_id")
    private Long id;
    private String name;
    @OneToMany(mappedBy = "mechanism", cascade = CascadeType.REMOVE, orphanRemoval = true)
    List<MechanismOfBoardGame> boardGame  = new ArrayList<>();
}
