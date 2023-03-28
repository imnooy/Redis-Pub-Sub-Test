package com.example.chattest.controller;

import com.example.chattest.model.ChatMessage;
import com.example.chattest.model.ChatRoom;
import com.example.chattest.service.ChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

//@RestController
//@RequestMapping("/chat")
//@RequiredArgsConstructor
public class ChatTestController {

//    private final ChatService chatService;
//
//    @PostMapping("/createRoom")
//    public String createRoom(@RequestParam String user1Id, @RequestParam String user2Id) {
//        return chatService.createRoom(user1Id, user2Id);
//    }
//
//    @GetMapping("/getRoom")
//    public Optional<ChatRoom> getRoom(@RequestParam String roomId) {
//        return chatService.getRoom(roomId);
//    }
//
//    @PostMapping("/sendMessage")
//    public void sendMessage(@RequestBody ChatMessage message) {
//        chatService.sendMessage(message);
//    }
//
//    @GetMapping("/getMessage")
//    public List<ChatMessage> getMessage(@RequestParam String roomId) {
//        return chatService.getMessage(roomId);
//    }
}
