package com.yeoboge.server.service.impl;

import com.yeoboge.server.domain.dto.boardGame.BoardGameListResponse;
import com.yeoboge.server.domain.dto.user.BookmarkListResponse;
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
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

/**
 * {@link UserService} 구현체
 */
@Slf4j
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

        BoardGameListResponse response = new BookmarkListResponse(new ArrayList<>());
        response.addBoardGames(bookmarks);

        return response;
    }
}
