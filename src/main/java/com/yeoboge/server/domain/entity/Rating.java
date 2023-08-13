package com.yeoboge.server.domain.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class Rating {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "rating_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "board_game_id")
    private BoardGame boardGame;

    @CreatedDate
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    private Double score;

    public void setParent(User user, BoardGame boardGame) {
        this.user = user;
        this.boardGame = boardGame;
    }

    public void setScore(Double score) {
        this.score = score;
    }
}
