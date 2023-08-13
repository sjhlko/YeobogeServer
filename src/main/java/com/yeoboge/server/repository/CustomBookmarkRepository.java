package com.yeoboge.server.repository;

import com.yeoboge.server.domain.dto.boardGame.BoardGameThumbnailDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * 북마크한 보드게임 관련 QueryDsl 쿼리 메서드를 정의하는 인터페이
 */
public interface CustomBookmarkRepository {
    /**
     * 회원이 찜한 보드게임 목록을 페이징하여 조회함.
     *
     * @param userId   조회를 요청한 회원 ID
     * @param pageable 페이징 정보가 담긴 {@link Pageable}
     * @return 페이징된 찜한 보드게임 {@link BoardGameThumbnailDto} 목록
     */
    Page<BoardGameThumbnailDto> getBookmarkByUserId(final Long userId, final Pageable pageable);
}
