package com.yeoboge.server.domain.vo;

import com.yeoboge.server.enums.BoardGameOrderColumn;
import org.springframework.data.domain.Sort;

public class MyBoardGamePageRequest {
    protected int page;
    protected int size;
    protected BoardGameOrderColumn sort;

    public void setPage(int page) {
        this.page = page < 0 ? 0 : page;
    }

    public void setSize(int size) {
        final int DEFAULT_SIZE = 20;
        final int MAX_SIZE = 50;
        this.size = size > MAX_SIZE ? DEFAULT_SIZE : size;
    }

    public void setSort(BoardGameOrderColumn sort) {
        this.sort = sort;
    }

    public org.springframework.data.domain.PageRequest of() {
        return org.springframework.data.domain.PageRequest.of(page, size, getSortColumn());
    }

    private Sort getSortColumn() {
        Sort.Direction direction = this.sort.getDirection();
        String column = this.sort.getColumn();

        return Sort.by(direction, column);
    }
}
