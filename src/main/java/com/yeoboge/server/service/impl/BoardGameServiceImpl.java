package com.yeoboge.server.service.impl;

import com.yeoboge.server.domain.dto.boardGame.BoardGameDetailResponse;
import com.yeoboge.server.domain.dto.boardGame.RatingRequest;
import com.yeoboge.server.domain.dto.boardGame.SearchBoardGameResponse;
import com.yeoboge.server.domain.entity.*;
import com.yeoboge.server.domain.vo.boardgame.SearchBoardGameRequest;
import com.yeoboge.server.domain.vo.response.MessageResponse;
import com.yeoboge.server.enums.error.BoardGameErrorCode;
import com.yeoboge.server.handler.AppException;
import com.yeoboge.server.repository.*;
import com.yeoboge.server.repository.customRepository.BoardGameCustomRepository;
import com.yeoboge.server.service.BoardGameService;
import com.yeoboge.server.utils.CsvParsing;
import com.yeoboge.server.utils.GetKorName;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * {@link BoardGameService} 구현체
 */
@Service
@RequiredArgsConstructor
public class BoardGameServiceImpl implements BoardGameService {
    private final BoardGameRepository boardGameRepository;
    private final UserRepository userRepository;
    private final ThemeRepository themeRepository;
    private final GenreRepository genreRepository;
    private final MechanismRepository mechanismRepository;
    private final MechanismOfBoardGameRepository mechanismOfBoardGameRepository;
    private final ThemeOfBoardGameRepository themeOfBoardGameRepository;
    private final GenreOfBoardGameRepository genreOfBoardGameRepository;
    private final BoardGameCustomRepository boardGameCustomRepository;

    @Override
    public void saveBoardGame() throws IOException {
        CsvParsing boardGameCsv = new CsvParsing("file path");
        String[] line = null;
        int lineCount = 0;
        int boardGameCount = 0;
        while ((line = boardGameCsv.nextRead()) != null) {
            //장르(카테고리)
            ArrayList<Integer> categorys = new ArrayList<>();
            if (lineCount == 0) {
                lineCount++;
                continue;
            }
            System.out.println(line[0]);
            BoardGame boardGame = BoardGame.builder()
                    .id(Long.parseLong(line[0]))
                    .name(line[1])
                    .description(line[2])
                    .playerMin(Integer.parseInt(line[8]))
                    .playerMax(Integer.parseInt(line[9]))
                    .weight(line[4])
                    .imagePath(line[30])
                    .playTime(Integer.parseInt(line[18]))
                    .isLocalized(IsLocalized.NO)
                    .build();
            RestTemplate restTemplate = new RestTemplate();
            String apiUrl = "https://api.geekdo.com/xmlapi/boardgame/"+ line[0];
            boolean isValid = false;
            boolean isSaved = false;
            for (int i = 0; i < 8; i++) {
                if (line[40+i].equals("1")){
                    categorys.add(i);
                    isValid=true;
                    if(!isSaved) {
                        boardGame = boardGameRepository.save(boardGame);
                        isSaved = true;
                    }
                    Optional<Genre> genre = genreRepository.findById(Long.parseLong(String.valueOf(i+1)));
                    GenreOfBoardGame genreOfBoardGame = GenreOfBoardGame.builder()
                            .genre(genre.get())
                            .boardGame(boardGame)
                            .build();
                    genreOfBoardGameRepository.save(genreOfBoardGame);
                }
            }

            // csv 에 카테고리 없을때
            if (!isValid) {
                String xmlData = restTemplate.getForObject(apiUrl, String.class);
                ArrayList<String> category = GetKorName.getCategory(xmlData);
                String korName = GetKorName.getKorName(xmlData);

                //카테고리 있는지 확인
                for (String s : category) {
                    Optional<Genre> genre = genreRepository.findByName(s);
                    if (genre.isEmpty()) continue;

                    //카테고리 있을때
                    GenreOfBoardGame genreOfBoardGame = GenreOfBoardGame.builder()
                            .boardGame(boardGame)
                            .genre(genre.get())
                            .build();
                    boardGame = boardGameRepository.save(boardGame);
                    genreOfBoardGameRepository.save(genreOfBoardGame);
                }

                //한글화 됐을때
                if (!korName.equals("")) {
                    boardGameCount++;
                    boardGame.updateIsLocalized(IsLocalized.YES);
                    boardGame.updateName(korName);
                    boardGameRepository.save(boardGame);
                }
                continue;
            }

            //csv 에 카테고리 있을때
            String xmlData = restTemplate.getForObject(apiUrl, String.class);
            String korName = GetKorName.getKorName(xmlData);

            //한글화 됐을때
            if (!korName.equals("")) {
                boardGameCount++;
                boardGame.updateName(korName);
                boardGame.updateIsLocalized(IsLocalized.YES);
                boardGameRepository.save(boardGame);
            }
        }
    }

    @Override
    public void saveTheme() throws IOException {
        CsvParsing boardGameCsv = new CsvParsing("file path");
        String[] line = null;
        int lineCount = 0;
        while ((line = boardGameCsv.nextRead()) != null) {
            if (lineCount == 0) {
                for (String s : line) {
                    if (s.equals("BGGId")) continue;
                    Theme theme = Theme.builder()
                            .name(s)
                            .build();
                    themeRepository.save(theme);
                }
                return;
            }
        }
    }


    @Override
    public void saveThemeOfBoardGame() throws IOException {
        CsvParsing boardGameCsv = new CsvParsing("file path");
        String[] line = null;
        int lineCount = 0;
        while ((line = boardGameCsv.nextRead()) != null) {
            if (lineCount == 0) {
                lineCount++;
                continue;
            }
            System.out.println("theme:" + line[0]);
            for (int i = 1; i < line.length; i++) {
                if (line[i].equals("0")) continue;
                Optional<Theme> theme = themeRepository.findById(Long.parseLong(String.valueOf(i)));
                Optional<BoardGame> boardGame = boardGameRepository.findById(Long.parseLong(line[0]));
                if(boardGame.isEmpty()) continue;
                ThemeOfBoardGame themeOfBoardGame = ThemeOfBoardGame.builder()
                        .boardGame(boardGame.get())
                        .theme(theme.get())
                        .build();
                themeOfBoardGameRepository.save(themeOfBoardGame);
            }
        }
    }

    @Override
    public void saveMechanismOfBoardGame() throws IOException {
        CsvParsing boardGameCsv = new CsvParsing("file path");
        String[] line = null;
        int lineCount = 0;
        while ((line = boardGameCsv.nextRead()) != null) {
            if (lineCount == 0) {
                lineCount++;
                continue;
            }
            System.out.println("mechanism:" + line[0]);
            for (int i = 1; i < line.length; i++) {
                if (line[i].equals("0")) continue;
                Optional<Mechanism> mechanism = mechanismRepository.findById(Long.parseLong(String.valueOf(i)));
                Optional<BoardGame> boardGame = boardGameRepository.findById(Long.parseLong(line[0]));
                if(boardGame.isEmpty()) continue;
                MechanismOfBoardGame mechanismOfBoardGame = MechanismOfBoardGame.builder()
                        .boardGame(boardGame.get())
                        .mechanism(mechanism.get())
                        .build();
                mechanismOfBoardGameRepository.save(mechanismOfBoardGame);
            }
        }
    }


    @Override
    public BoardGameDetailResponse getBoardGameDetail(Long id) {
        List<String> genre = new ArrayList<>();
        List<String> theme = new ArrayList<>();
        List<String> mechanism = new ArrayList<>();
        BoardGame boardGame = boardGameRepository.findById(id)
                .orElseThrow(() -> new AppException(BoardGameErrorCode.BOARD_GAME_NOT_FOUND));
        for (ThemeOfBoardGame themeOfBoardGame : boardGame.getTheme()) {
            theme.add(themeOfBoardGame.getTheme().getName());
        }
        for (GenreOfBoardGame genreOfBoardGame : boardGame.getGenre()) {
            genre.add(genreOfBoardGame.getGenre().getName());
        }
        for (MechanismOfBoardGame mechanismOfBoardGame : boardGame.getMechanism()) {
            mechanism.add(mechanismOfBoardGame.getMechanism().getName());
        }
        return BoardGameDetailResponse.of(boardGame,theme,genre,mechanism);
    }

    @Override
    public MessageResponse addBookmark(Long id, Long userId) {
        BoardGame boardGame = boardGameRepository.getById(id);
        User user = userRepository.getByIdFetchBookmark(userId);

        user.addBookmark(boardGame);
        userRepository.save(user);

        return MessageResponse.builder()
                .message("찜하기가 저장되었습니다")
                .build();
    }

    @Override
    public void removeBookmark(Long id, Long userId) {
        BoardGame boardGame = boardGameRepository.getById(id);
        User user = userRepository.getByIdFetchBookmark(userId);

        user.removeBookmark(boardGame);
        userRepository.save(user);
    }

    @Override
    public MessageResponse rateBoardGame(Long id, Long userId, RatingRequest request) {
        BoardGame boardGame = boardGameRepository.getById(id);
        User user = userRepository.getByIdFetchRating(userId);
        Double score = request.score();

        if (score != 0) user.rateBoardGame(boardGame, score);
        else user.removeRating(boardGame);
        userRepository.save(user);

        return MessageResponse.builder()
                .message("평가가 저장되었습니다.")
                .build();
    }

    @Override
    public Page<SearchBoardGameResponse> searchBoardGame(
            Pageable pageable,
            SearchBoardGameRequest request
    ) {
        Page<BoardGame> searchResults = boardGameCustomRepository
                .findBoardGameBySearchOption(pageable,request);
        Page<SearchBoardGameResponse> responses = searchResults.map(SearchBoardGameResponse::of);
        return responses;
    }
}
