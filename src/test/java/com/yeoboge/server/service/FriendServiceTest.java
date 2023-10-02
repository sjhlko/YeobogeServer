package com.yeoboge.server.service;

import com.yeoboge.server.domain.dto.PageResponse;
import com.yeoboge.server.domain.dto.user.UserInfoDto;
import com.yeoboge.server.repository.FriendRepository;
import com.yeoboge.server.repository.FriendRequestRepository;
import com.yeoboge.server.service.impl.FriendServiceImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class FriendServiceTest {
    @InjectMocks
    private FriendServiceImpl friendService;

    @Mock
    private FriendRepository friendRepository;
    @Mock
    private FriendRequestRepository friendRequestRepository;

    @Test
    @DisplayName("친구 목록 조회 성공: 첫 번째 페이지")
    public void getFriendsFirstPageSuccess() {
        // given
        Long userId = 1L;
        Pageable pageable = PageRequest.of(0, 1);
        List<UserInfoDto> friends = getFriendDtoList();
        Page page = getFriendDtoPage(pageable, 5);

        // when
        when(friendRepository.getFriendsPage(userId, pageable)).thenReturn(page);

        PageResponse actual = friendService.getFriends(userId, pageable);

        // then
        assertThat(actual.getContent()).isEqualTo(friends);
        assertThat(actual.getPrevPage()).isNull();
        assertThat(actual.getNextPage()).isNotNull();
    }

    @Test
    @DisplayName("친구 목록 조회 성공: 중간 페이지")
    public void getFriendsMiddlePageSuccess() {
        // given
        Long userId = 1L;
        Pageable pageable = PageRequest.of(1, 1);
        List<UserInfoDto> friends = getFriendDtoList();
        Page page = getFriendDtoPage(pageable, 5);

        // when
        when(friendRepository.getFriendsPage(userId, pageable)).thenReturn(page);

        PageResponse actual = friendService.getFriends(userId, pageable);

        // then
        assertThat(actual.getContent()).isEqualTo(friends);
        assertThat(actual.getPrevPage()).isNotNull();
        assertThat(actual.getNextPage()).isNotNull();
    }

    @Test
    @DisplayName("친구 목록 조회 성공: 마지막 페이지")
    public void getFriendsLastPageSuccess() {
        // given
        Long userId = 1L;
        Pageable pageable = PageRequest.of(4, 1);
        List<UserInfoDto> friends = getFriendDtoList();
        Page page = getFriendDtoPage(pageable, 5);

        // when
        when(friendRepository.getFriendsPage(userId, pageable)).thenReturn(page);

        PageResponse actual = friendService.getFriends(userId, pageable);

        // then
        assertThat(actual.getContent()).isEqualTo(friends);
        assertThat(actual.getPrevPage()).isNotNull();
        assertThat(actual.getNextPage()).isNull();
    }

    @Test
    @DisplayName("친구 요청 목록 조회 성공: 첫 번째 페이지")
    public void getFriendsRequestsFirstPageSuccess() {
        // given
        Long userId = 1L;
        Pageable pageable = PageRequest.of(0, 1);
        List<UserInfoDto> friends = getFriendDtoList();
        Page page = getFriendDtoPage(pageable, 5);

        // when
        when(friendRequestRepository.getFriendRequestsPage(userId, pageable)).thenReturn(page);

        PageResponse actual = friendService.getFriendRequests(userId, pageable);

        // then
        assertThat(actual.getContent()).isEqualTo(friends);
        assertThat(actual.getPrevPage()).isNull();
        assertThat(actual.getNextPage()).isNotNull();
    }

    @Test
    @DisplayName("친구 요청 목록 조회 성공: 중간 페이지")
    public void getFriendsRequestsMiddlePageSuccess() {
        // given
        Long userId = 1L;
        Pageable pageable = PageRequest.of(1, 1);
        List<UserInfoDto> friends = getFriendDtoList();
        Page page = getFriendDtoPage(pageable, 5);

        // when
        when(friendRequestRepository.getFriendRequestsPage(userId, pageable)).thenReturn(page);

        PageResponse actual = friendService.getFriendRequests(userId, pageable);

        // then
        assertThat(actual.getContent()).isEqualTo(friends);
        assertThat(actual.getPrevPage()).isNotNull();
        assertThat(actual.getNextPage()).isNotNull();
    }

    @Test
    @DisplayName("친구 요청 목록 조회 성공: 마지막 페이지")
    public void getFriendsRequestsLastPageSuccess() {
        // given
        Long userId = 1L;
        Pageable pageable = PageRequest.of(4, 1);
        List<UserInfoDto> friends = getFriendDtoList();
        Page page = getFriendDtoPage(pageable, 5);

        // when
        when(friendRequestRepository.getFriendRequestsPage(userId, pageable)).thenReturn(page);

        PageResponse actual = friendService.getFriendRequests(userId, pageable);

        // then
        assertThat(actual.getContent()).isEqualTo(friends);
        assertThat(actual.getPrevPage()).isNotNull();
        assertThat(actual.getNextPage()).isNull();
    }

    private Page<UserInfoDto> getFriendDtoPage(Pageable pageable, int total) {
        List<UserInfoDto> content = getFriendDtoList();
        return new PageImpl<>(content, pageable, total);
    }

    private List<UserInfoDto> getFriendDtoList() {
        UserInfoDto dto = UserInfoDto.builder()
                .id(1L)
                .nickname("nickname")
                .imagePath("image_path")
                .build();
        return List.of(dto);
    }
}
