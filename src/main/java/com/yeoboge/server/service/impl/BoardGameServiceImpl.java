package com.yeoboge.server.service.impl;

import com.yeoboge.server.domain.dto.boardGame.BoardGameDetailResponse;
import com.yeoboge.server.domain.dto.user.BookmarkResponse;
import com.yeoboge.server.domain.entity.BoardGame;
import com.yeoboge.server.domain.entity.GenreOfBoardGame;
import com.yeoboge.server.domain.entity.ThemeOfBoardGame;
import com.yeoboge.server.domain.entity.User;
import com.yeoboge.server.domain.vo.response.MessageResponse;
import com.yeoboge.server.enums.error.BoardGameErrorCode;
import com.yeoboge.server.handler.AppException;
import com.yeoboge.server.repository.BoardGameRepository;
import com.yeoboge.server.repository.UserRepository;
import com.yeoboge.server.service.BoardGameService;
import com.yeoboge.server.utils.CsvParsing;
import com.yeoboge.server.utils.GetKorName;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * {@link BoardGameService} 구현체
 */
@Service
@RequiredArgsConstructor
public class BoardGameServiceImpl implements BoardGameService {
    private final BoardGameRepository boardGameRepository;
    private final UserRepository userRepository;

    @Override
    public void saveBoardGame() throws IOException {
        CsvParsing boardGameCsv = new CsvParsing("C:\\Users\\HeongJi\\Desktop\\여보게\\games.txt");
        String[] line = null;
        int lineCount = 0;
        int boardGameCount = 0;
        while ((line = boardGameCsv.nextRead()) != null) {
            if (lineCount == 0) {
                System.out.println(line[40]);
                lineCount++;
                continue;
            }
            RestTemplate restTemplate = new RestTemplate();
            String apiUrl = "https://api.geekdo.com/xmlapi/boardgame/"+ line[0];
            boolean isValid = false;
            for (int i = 0; i < 8; i++) {
                if (line[40+i].equals("1")){
                    isValid=true;
                    break;
                }
            }
            if (!isValid) continue;
            String xmlData = restTemplate.getForObject(apiUrl, String.class);
            String korName = GetKorName.getKorName(xmlData);
            if (!korName.equals("")) {
                boardGameCount++;
                System.out.println("한글화됨");
                System.out.println(korName);
            }
        }
        System.out.println("전체 갯수 : " + boardGameCount);
    }

    @Override
    public BoardGameDetailResponse getBoardGameDetail(Long id) {
        List<String> genre = new ArrayList<>();
        List<String> theme = new ArrayList<>();
        BoardGame boardGame = boardGameRepository.findById(id)
                .orElseThrow(() -> new AppException(BoardGameErrorCode.BOARD_GAME_NOT_FOUND));
        for (ThemeOfBoardGame themeOfBoardGame : boardGame.getTheme()) {
            theme.add(themeOfBoardGame.getTheme().getName());
        }
        for (GenreOfBoardGame genreOfBoardGame : boardGame.getGenre()) {
            genre.add(genreOfBoardGame.getGenre().getName());
        }
        return BoardGameDetailResponse.of(boardGame,theme,genre);
    }

    @Override
    public BookmarkResponse addBookmark(Long id, Long userId) {
        BoardGame boardGame = boardGameRepository.getById(id);
        User user = userRepository.getById(userId);

        user.addBookmark(boardGame);
        userRepository.save(user);

        return BookmarkResponse.builder()
                .userId(userId)
                .boardGameId(id)
                .build();
    }

    @Override
    public void removeBookmark(Long id, Long userId) {
        BoardGame boardGame = boardGameRepository.getById(id);
        User user = userRepository.getById(userId);

        user.removeBookmark(boardGame);
        userRepository.save(user);
    }

    @Override
    public MessageResponse rateBoardGame(Long id, Long userId, Double rate) {
        BoardGame boardGame = boardGameRepository.getById(id);
        User user = userRepository.getById(userId);

        if (rate != 0) user.rateBoardGame(boardGame, rate);
        else user.removeRating(boardGame);
        userRepository.save(user);

        return MessageResponse.builder()
                .message("평가가 저장되었습니다.")
                .build();
    }
}
