package com.yeoboge.server.service.impl;

import com.yeoboge.server.domain.dto.PageResponse;
import com.yeoboge.server.domain.dto.boardGame.BoardGameListResponse;
import com.yeoboge.server.domain.dto.boardGame.ThumbnailMapResponse;
import com.yeoboge.server.domain.dto.user.UserDetailResponse;
import com.yeoboge.server.domain.dto.user.UserUpdateRequest;
import com.yeoboge.server.domain.entity.BoardGame;
import com.yeoboge.server.domain.entity.User;
import com.yeoboge.server.domain.vo.PageRequest;
import com.yeoboge.server.domain.vo.response.MessageResponse;
import com.yeoboge.server.repository.BookmarkRepository;
import com.yeoboge.server.repository.RatingRepository;
import com.yeoboge.server.repository.UserRepository;
import com.yeoboge.server.service.S3FileUploadService;
import com.yeoboge.server.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.List;

/**
 * {@link UserService} 구현체
 */
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final BookmarkRepository bookmarkRepository;
    private final RatingRepository ratingRepository;
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
    public PageResponse getMyBookmarks(Long id, PageRequest pageRequest) {
        Pageable pageable = pageRequest.of();
        Page bookmarks = bookmarkRepository.getBookmarkByUserId(id, pageable);
        return new PageResponse(bookmarks);
    }

    @Override
    public BoardGameListResponse getMyAllRatings(Long id) {
        BoardGameListResponse response = new ThumbnailMapResponse(new HashMap<>());
        List<Double> ratingGroup = ratingRepository.getUserRatingGroup(id);

        for (Double rate : ratingGroup) {
            List<BoardGame> ratings = ratingRepository.getRatingByUserId(id, rate);
            response.addBoardGames(ratings, rate);
        }

        return response;
    }

    @Override
    public PageResponse getMyRatingsByScore(
            Long id, Double score, PageRequest pageRequest
    ) {
        Pageable pageable = pageRequest.of();
        Page ratings = ratingRepository.getRatingsByUserId(id, score, pageable);
        return new PageResponse(ratings);
    }
}
