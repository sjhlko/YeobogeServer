package com.yeoboge.server.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.data.domain.Sort.Direction;

import java.util.Arrays;

/**
 * 보드게임 목록 조회 시 클라이언트에서 넘겨주는 정렬 기준을
 * 테이블 컬럼명 및 정렬 방향에 매핑하기 위한 {@code Enum}
 */
@Getter
@AllArgsConstructor
public enum BoardGameOrderColumn {
    NEW("new", "createdAt", Direction.DESC),
    EASY("easy", "weight", Direction.ASC),
    HARD("hard", "weight", Direction.DESC),
    POPULAR("popular", "", Direction.DESC);

    private String criteria;
    private String column;
    private Direction direction;

    /**
     * {@code String} 타입의 정렬 기준을 {@code Enum} 형식으로 변환함.
     * @param criteria 보드게임 목록 정렬 기준
     * @return 해당 정렬 기준의 {@link BoardGameOrderColumn}
     */
    public static BoardGameOrderColumn of(String criteria) {
        if (criteria == null) throw new IllegalArgumentException();

        return Arrays.stream(BoardGameOrderColumn.values())
                .filter(b -> b.criteria.equals(criteria))
                .findAny()
                .orElseThrow(() -> new IllegalArgumentException());
    }
}
