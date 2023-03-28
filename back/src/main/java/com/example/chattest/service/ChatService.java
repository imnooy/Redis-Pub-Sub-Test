package com.example.chattest.service;

import com.example.chattest.model.ChatMessage;
import com.example.chattest.model.ChatRoom;
import com.example.chattest.repository.ChatRoomRepository;
//import com.example.chattest.repository.MessageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class ChatService {
    private final ChatRoomRepository chatRepository;
//    private final MessageRepository messageRepository;

    public String createRoom(Long user1, Long user2) {
//        String roomId = UUID.randomUUID().toString();
//        Set<Long> members = new HashSet<>();
//        members.add(user1);
//        members.add(user2);
//        ChatRoom chatRoom = new ChatRoom(roomId, members);
//        chatRepository.save(chatRoom);
//        return chatRoom.getId();
        return null;
    }

    public Optional<ChatRoom> getRoom(String roomId) {
        return chatRepository.findById(roomId);
    }

    public void sendMessage(ChatMessage message) {
//        String msgId = UUID.randomUUID().toString();
        ChatMessage chatMessage = new ChatMessage();
//        chatMessage.setId(msgId);
        chatMessage.setRoomId(message.getRoomId());
        chatMessage.setSenderId(message.getSenderId());
        chatMessage.setContent(message.getContent());
//        messageRepository.save(message);
//        redisTemplate.opsForList().rightPush(CHAT_ROOMS+message.getRoomId(), message);
    }

    public List<ChatMessage> getMessage(String roomId) {
//        return messageRepository.findByRoomId(roomId);

//        List<Object> keys = redisTemplate.opsForList().range(CHAT_ROOMS+roomId, 0, -1);
        return null;
    }
}

