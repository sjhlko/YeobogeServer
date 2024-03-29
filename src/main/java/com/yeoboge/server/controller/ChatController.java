package com.yeoboge.server.controller;

import com.yeoboge.server.domain.dto.PageResponse;
import com.yeoboge.server.domain.dto.chat.ChatRoomResponse;
import com.yeoboge.server.domain.vo.chat.ChatRoomIdResponse;
import com.yeoboge.server.domain.vo.response.Response;
import com.yeoboge.server.service.ChatMessageService;
import com.yeoboge.server.service.ChatRoomService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 채팅 관련 API 엔드포인트에 대해 매핑되는 Rest Controller
 */
@RestController
@RequestMapping("/chats")
@RequiredArgsConstructor
public class ChatController {
    private final ChatRoomService chatRoomService;
    private final ChatMessageService chatMessageService;

    /**
     * 두 회원의 채팅 방 아이디를 조회하는 API
     *
     * @param id 현재 로그인한 회원 ID
     * @param targetUserId 현재로그인한 회원과 채팅을 나눌 회원의 ID
     * @return 채팅방 id 정보를 담은 {@link ChatRoomIdResponse}
     */
    @GetMapping("/request-room/{targetUserId}")
    public Response<ChatRoomIdResponse> getChatRooms(
            @AuthenticationPrincipal Long id,
            @PathVariable Long targetUserId
    ) {
        ChatRoomIdResponse response = chatRoomService.getChatRoomId(id, targetUserId);
        return Response.success(response);
    }

    /**
     * 회원의 채팅방 목록을 조회하는 API
     *
     * @param id 현재 로그인한 회원 ID
     * @return 채팅방 정보를 담은 {@link ChatRoomResponse} 리스트
     */
    @GetMapping()
    public Response<List<ChatRoomResponse>> getChatRooms(@AuthenticationPrincipal Long id) {
        List<ChatRoomResponse> response = chatRoomService.getChatRooms(id);
        return Response.success(response);
    }

    /**
     * 회원의 채팅 목록을 조회하는 API
     *
     * @param id       현재 로그인한 회원 ID
     * @param pageable 페이징 관련 파라미터를 받기 위한 인터페이스 {@link Pageable}
     * @return 페이징된 채팅 정보 리스트와 페이지 정보를 담은 {@link PageResponse}
     */
    @GetMapping("/{id}")
    public Response<PageResponse> getChatMessages(
            @AuthenticationPrincipal Long currentUserId,
            @PageableDefault(
                    size = 20,
                    sort = "createdAt",
                    direction = Sort.Direction.DESC
            ) Pageable pageable,
            @PathVariable Long id
    ) {
        PageResponse response = chatMessageService.getChatMessages(currentUserId, id, pageable);
        return Response.success(response);
    }
}
