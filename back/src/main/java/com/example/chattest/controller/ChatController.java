package com.example.chattest.controller;

import com.example.chattest.model.ChatMessage;
import com.example.chattest.model.ChatRoom;
import com.example.chattest.repository.ChatRepository;
import com.example.chattest.service.PublisherService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class ChatController {
    private final PublisherService publisherService;

    private final ChatRepository chatRepository;

    /**
     * @MessageMapping("url") : url로 들어오는 메시지 매핑
     * "/pub/chat/message" 으로 들어오는 message를 ChatMessage으로 바인딩하여 실행
     */
    @MessageMapping("/chat/message")
    public void message(ChatMessage message) {
        ChannelTopic topic = chatRepository.getMsgTopic(message.getRoomId());
        if(topic == null) {
            topic = new ChannelTopic(message.getRoomId());
            chatRepository.addTopic(topic);
        }
        /** 토픽 가져와서 해당 방에 message 전달 **/
        publisherService.msgPublish(topic, message);
    }

    @MessageMapping("/chat/rooms")
    public void room(ChatRoom message) {
        ChannelTopic topic = chatRepository.getRoomsTopic();
        publisherService.roomPublish(topic, message);
    }
}
