package com.yeoboge.server.enums;

import org.springframework.core.convert.converter.Converter;

/**
 * 클라이언트의 HTTP 요청에서 정렬 기준이 {@code String} 타입으로 넘겨지는 것을
 * {@link BoardGameOrderColumn}으로 변환하는 {@link Converter}
 */
public class BoardGameOrderColumnConverter implements Converter<String, BoardGameOrderColumn> {
    @Override
    public BoardGameOrderColumn convert(String source) {
        return BoardGameOrderColumn.of(source);
    }
}
