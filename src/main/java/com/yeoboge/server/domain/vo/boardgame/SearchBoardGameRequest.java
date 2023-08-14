package com.yeoboge.server.domain.vo.boardgame;

import java.util.ArrayList;

/**
 * 보드게임 요청 시 쿼리 파라미터로 받을 변수들을 저장하는 VO
 *
 * @param player 최소 인원수
 * @param searchWord 찾을 보드게임 이름의 일부
 * @param genre 찾을 보드게임의 장르
 */
public record SearchBoardGameRequest(
       Integer player,
       String searchWord,
       ArrayList<Long> genre

) { }
