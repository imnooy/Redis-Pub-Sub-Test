package com.example.chattest.service;

import com.example.chattest.model.ChatMessage;
import com.example.chattest.model.ChatRoom;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PublisherService {
    private final RedisTemplate redisTemplate;

    private ListOperations<String, ChatMessage> opsListChatMessages;
    private HashOperations<String,String, ChatRoom> opsHashChatRoom;
    private static final String ROOMS = "Rooms";

    @PostConstruct
    private void init(){
        /** redisTemplate에서 operation 받기 **/
        opsListChatMessages = redisTemplate.opsForList();
        opsHashChatRoom = redisTemplate.opsForHash();
    }

    /**
     * subscriber한테 메세지 전달
     */
    public void msgPublish(ChannelTopic topic, ChatMessage message){
        Long id = opsListChatMessages.size(message.getRoomId());
        message.setId(id);
        message.setTimestamp(LocalDateTime.now());

        String roomId = message.getRoomId();
        ChatRoom chatRoom = opsHashChatRoom.get(ROOMS, roomId);
        Map<Long, Long> lastReadIndex = chatRoom.getLastReadIndex();
        Long lastIndex = lastReadIndex.get(message.getSenderId() );
        lastReadIndex.put(message.getSenderId(), lastIndex + 1);

        opsListChatMessages.rightPush(message.getRoomId(), message);

        /** 채널 topic에 메시지 보내기 **/
        redisTemplate.convertAndSend(topic.getTopic(), message);
    }

    public void roomPublish(ChannelTopic topic, ChatRoom message) {
        String id = UUID.randomUUID().toString(); //룸id 랜덤 생성
        message.setId(id);
        Set<Long> userIds = message.getUserIds();
        Map<Long, Long> lastReadIndex = new HashMap<>();
        userIds.forEach(userId -> {
            lastReadIndex.put(userId, 0L);
        });
        message.setLastReadIndex(lastReadIndex);
        message.setMsgs(0L);
        message.setContent("채팅을 시작해주세요!");

        opsHashChatRoom.put(ROOMS, message.getId(),message);
        redisTemplate.convertAndSend(topic.getTopic(), message);
    }
}
