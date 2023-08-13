package com.yeoboge.server.service;

import com.yeoboge.server.domain.dto.boardGame.BoardGameDetailResponse;
import com.yeoboge.server.domain.dto.boardGame.RatingRequest;
import com.yeoboge.server.domain.dto.boardGame.SearchBoardGameResponse;
import com.yeoboge.server.domain.vo.boardgame.SearchBoardGameRequest;
import com.yeoboge.server.domain.vo.response.MessageResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.io.IOException;

/**
 * 보드게임 관련 비즈니스 로직에 대한 메서드를 제공하는 인터페이스
 */
public interface BoardGameService {

    /**
     * 보드게임을 저장하는 메서드
     */
    void saveBoardGame() throws IOException;

    /**
     * 테마를 저장하는 메서드
     */
    void saveTheme() throws IOException;

    /**
     * 보드게임별 테마를 저장하는 메서드
     */
    void saveThemeOfBoardGame() throws IOException;

    /**
     * 보드게임별 전략을 저장하는 메서드
     */
    void saveMechanismOfBoardGame() throws IOException;


    /**
     * 특정 보드게임에 대한 정보를 반환하는 메서드
     *
     * @param id 정보를 반환할 보드게임의 id
     * @return {@link BoardGameDetailResponse}
     */
    BoardGameDetailResponse getBoardGameDetail(Long id);

    /**
     * 특정 보드게임을 찜 목록에 추가함.
     *
     * @param id     {@link com.yeoboge.server.domain.entity.BoardGame} ID
     * @param userId {@link com.yeoboge.server.domain.entity.User} ID
     * @return 찜하기 성공 메세지 {@link MessageResponse}
     */
    MessageResponse addBookmark(Long id, Long userId);

    /**
     * 찜한 보드게임을 취소함.
     *
     * @param id 찜하기 취소할 보드게임 ID
     * @param userId 취소할 사용자 ID
     */
    void removeBookmark(Long id, Long userId);

    /**
     * 보드게임의 평점을 남김.
     *
     * @param id 평가할 보드게임 ID
     * @param userId 평점을 남길 사용자 ID
     * @param request 보드게임 평점 DTO {@link RatingRequest}
     * @return {@link MessageResponse}
     */
    MessageResponse rateBoardGame(Long id, Long userId, RatingRequest request);

    /**
     * 보드게임을 검색한다.
     *
     * @param pageable 페이징 관련한 정보가 담긴 {@link Pageable}
     * @param request 보드게임 검색 시의 검색 조건이 담긴 DTO {@link SearchBoardGameRequest}
     * @return 검색 조건에 부합하는 보드게임에 대한 정보가 담긴 {@link SearchBoardGameResponse} DTO를
     * 페이징을 적용하여 리턴함
     */
    Page<SearchBoardGameResponse> searchBoardGame(Pageable pageable,
                                                  SearchBoardGameRequest request);
}
