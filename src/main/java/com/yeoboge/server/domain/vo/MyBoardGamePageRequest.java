package com.yeoboge.server.domain.vo;

import com.yeoboge.server.enums.BoardGameOrderColumn;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.PageRequest;

/**
 * 찜한 보드게임, 평가한 보드게임 조회 시 동적인 정렬 기준을 함께 받기 위한
 * 커스텀 {@link PageRequest}
 */
public class MyBoardGamePageRequest {
    protected int page;
    protected int size;
    protected BoardGameOrderColumn sort;

    /**
     * 페이지 번호를 지정함.
     *
     * @param page 페이지 번호
     */
    public void setPage(int page) {
        this.page = page < 0 ? 0 : page;
    }

    /**
     * 조회할 데이터의 크기를 지정함.
     *
     * @param size 페이징 시 조회할 데이터 수
     */
    public void setSize(int size) {
        final int DEFAULT_SIZE = 20;
        final int MAX_SIZE = 50;
        this.size = size > MAX_SIZE ? DEFAULT_SIZE : size;
    }

    /**
     * 정렬 기준을 지정함.
     *
     * @param sort 정렬 기준 {@link BoardGameOrderColumn}
     */
    public void setSort(BoardGameOrderColumn sort) {
        this.sort = sort;
    }

    /**
     * 커스텀된 페이징 정보를 토대로 {@link PageRequest}를 반환함.
     * @return
     */
    public PageRequest of() {
        return PageRequest.of(page, size, getSortColumn());
    }

    /**
     * {@code Enum} 타입의 정렬 기준을 Spring JPA의 정렬 객체 {@link Sort}로 변환함.
     *
     * @return 정렬 기준이 지정된 {@link Sort}
     */
    private Sort getSortColumn() {
        Sort.Direction direction = this.sort.getDirection();
        String column = this.sort.getColumn();

        return Sort.by(direction, column);
    }
}
