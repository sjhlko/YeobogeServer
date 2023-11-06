package com.yeoboge.server.domain.entity;

import com.yeoboge.server.enums.error.CommonErrorCode;
import com.yeoboge.server.handler.AppException;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

@Getter
@AllArgsConstructor
public enum Weight {
    VERY_EASY(0, "매우 쉬움"),
    EASY(1, "쉬움"),
    MEDIUM(2, "보통"),
    HARD(3, "어려움"),
    VERY_HARD(4, "매우 어려움");

    int index;
    String complexity;

    public static Weight valueOf(int index) {
        return Arrays.stream(Weight.values())
                .filter(k -> k.index == index)
                .findFirst()
                .orElseThrow(() -> new AppException(CommonErrorCode.INTERNAL_SERVER_ERROR));
    }
}
