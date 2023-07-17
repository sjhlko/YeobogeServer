package com.yeoboge.server.domain.entity;

import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class RefreshToken {
    @Id
    private String refreshToken;
    private Long userId;
}
