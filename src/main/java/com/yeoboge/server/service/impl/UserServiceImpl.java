package com.yeoboge.server.service.impl;

import com.yeoboge.server.domain.dto.boardGame.BoardGameListResponse;
import com.yeoboge.server.domain.dto.boardGame.ThumbnailMapResponse;
import com.yeoboge.server.domain.dto.boardGame.ThumbnailListResponse;
import com.yeoboge.server.domain.dto.user.UserDetailResponse;
import com.yeoboge.server.domain.dto.user.UserUpdateRequest;
import com.yeoboge.server.domain.entity.BoardGame;
import com.yeoboge.server.domain.entity.User;
import com.yeoboge.server.domain.vo.response.MessageResponse;
import com.yeoboge.server.enums.BoardGameOrderColumn;
import com.yeoboge.server.enums.error.AuthenticationErrorCode;
import com.yeoboge.server.handler.AppException;
import com.yeoboge.server.repository.BoardGameRepository;
import com.yeoboge.server.repository.UserRepository;
import com.yeoboge.server.service.S3FileUploadService;
import com.yeoboge.server.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * {@link UserService} 구현체
 */
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final BoardGameRepository boardGameRepository;
    private final S3FileUploadService s3FileUploadService;
    @Override
    public UserDetailResponse getProfile(Long id) {
        User user = userRepository.getById(id);
        return UserDetailResponse.of(user);
    }

    @Override
    public MessageResponse updateUser(MultipartFile file, UserUpdateRequest request, Long id) {
        User existedUser = userRepository.getById(id);
        if (file!=null) existedUser.updateUserProfile(s3FileUploadService.uploadFile(file), request.nickname());
        else if (request.isChanged())
            existedUser.updateUserProfile(null, request.nickname());
        else existedUser.updateUserProfile(existedUser.getProfileImagePath(), request.nickname());
        userRepository.save(existedUser);
        return MessageResponse.builder()
                .message("프로필 변경 성공")
                .build();

    }

    @Override
    public BoardGameListResponse getMyBookmarks(Long id, Integer page, BoardGameOrderColumn order) {
        List<BoardGame> bookmarks = boardGameRepository.getBookmarkByUserId(id, page, order);

        BoardGameListResponse response = new ThumbnailListResponse(new ArrayList<>());
        response.addBoardGames(bookmarks);

        return response;
    }

    @Override
    public BoardGameListResponse getMyAllRatings(Long id) {
        BoardGameListResponse response = new ThumbnailMapResponse(new HashMap<>());
        List<Double> ratingGroup = boardGameRepository.getUserRatingGroup(id);

        for (Double rate : ratingGroup) {
            List<BoardGame> ratings = boardGameRepository.getRatingByUserId(id, rate);
            response.addBoardGames(ratings, rate);
        }

        return response;
    }

    @Override
    public BoardGameListResponse getMyRatingsByScore(
            Long id, Double score, Integer page, BoardGameOrderColumn order
    ) {
        List<BoardGame> boardGames = boardGameRepository.getRatingsByUserId(id, score, page, order);
        return makeListResponse(boardGames);
    }

    /**
     * 보드게임 목록을 리스트화하여 담은 DTO 객체를 반환함.
     *
     * @param boardGames 원본 보드게임 리스트
     * @return {@link ThumbnailListResponse}
     */
    private BoardGameListResponse makeListResponse(List<BoardGame> boardGames) {
        BoardGameListResponse response = new ThumbnailListResponse(new ArrayList<>());
        response.addBoardGames(boardGames);

        return response;
    }
}
