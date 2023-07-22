package com.yeoboge.server.domain.entity;

import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class Token {
    @Id
    private String accessToken;
    private String refreshToken;
}
