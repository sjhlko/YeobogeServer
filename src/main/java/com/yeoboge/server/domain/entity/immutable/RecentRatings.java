package com.yeoboge.server.domain.entity.immutable;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Immutable;

@Entity
@Immutable
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class RecentRatings {
    @Id
    private Long boardGameId;
    private Integer ratings;
}
