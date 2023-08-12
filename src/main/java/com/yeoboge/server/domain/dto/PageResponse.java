package com.yeoboge.server.domain.dto;

import lombok.Getter;
import org.springframework.data.domain.Page;

import java.util.List;

/**
 * 페이징 관련 API의 응답을 전달하기 위한 DTO
 *
 * @param <T> 리스트로 전달할 엔티티의 DTO 클래스 타입
 */
@Getter
public class PageResponse<T> {
    private List<T> content;
    private Integer prevPage;
    private Integer nextPage;

    /**
     * 페이징된 {@code <T>} DTO를 포함하여 페이지 정보가 저장된
     * {@link Page}에서 필요한 데이터를 가져오는 생성자
     *
     * @param page DTO의 {@link Page}
     */
    public PageResponse(Page<T> page) {
        this.content = page.getContent();
        this.prevPage = page.isFirst() ? null : page.getNumber() - 1;
        this.nextPage = page.isLast() ? null : page.getNumber() + 1;
    }
}
