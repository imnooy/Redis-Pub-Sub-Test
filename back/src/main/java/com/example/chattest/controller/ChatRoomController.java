package com.example.chattest.controller;

import com.example.chattest.model.ChatMessage;
import com.example.chattest.model.ChatRoom;
import com.example.chattest.repository.ChatRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/chat")
public class ChatRoomController {
    private final ChatRepository chatRepository;
    @GetMapping("/rooms")
    @ResponseBody
    public List<ChatRoom> room() {
        return chatRepository.findAllRoom();
    }

    /**
     * 채팅 룸 생성
     */
    @PostMapping("/room")
    @ResponseBody
    public String createRoom(@RequestParam Long user1, @RequestParam Long user2) {
        return chatRepository.createChatRoom(user1, user2);
    }

    /**
     * 채팅방 찾기
     */
    @GetMapping("/room/{roomId}")
    @ResponseBody
    public ChatRoom roomInfo(@PathVariable String roomId) {
        return chatRepository.findRoomById(roomId);
    }

    @GetMapping("/room/{roomId}/messages")
    @ResponseBody
    public List<ChatMessage> messages(@PathVariable String roomId) {
        return chatRepository.findByRoomId(roomId);
    }
}
