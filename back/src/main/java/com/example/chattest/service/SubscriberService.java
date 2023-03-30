package com.example.chattest.service;

import com.example.chattest.model.ChatMessage;
import com.example.chattest.model.ChatRoom;
import com.example.chattest.model.ReadDto;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class SubscriberService implements MessageListener {

    private final RedisTemplate redisTemplate;

    private final ObjectMapper objectMapper;

    /** 어디서든지 message를 보낼 수 있는 클래스**/
    private final SimpMessageSendingOperations messageTemplate;

    /**
     *  메시지 publish되면 대기하고 있던 onMessage가 해당 메시지를 받아 처리
     */
    @Override
    public void onMessage(Message message, byte[] pattern) {
        /**redis에 publish 된 데이터 받아서 deserialize**/
        String publishedMessge = (String) redisTemplate.getStringSerializer().deserialize(message.getBody());
        if(tryMsg(publishedMessge) != null) {
            recvMsg(tryMsg(publishedMessge));
        }
        else if(tryRead(publishedMessge)!=null) {
            recvReadDto(tryRead(publishedMessge));
        }
        else {
            ChatRoom chatRoom = tryRoom(publishedMessge);
            recvRoom(chatRoom);
        }
    }

    public ReadDto tryRead(String msg) {
        ReadDto readDto = null;
        try {
            readDto = objectMapper.readValue(msg, ReadDto.class);
        } catch(Exception ex) {
            return null;
        }
        if(readDto == null || (readDto!=null && readDto.getLastReadIndex()==null)) {
            return null;
        }
        return readDto;
    }

    public ChatMessage tryMsg(String msg) {
        ChatMessage chatMessage = null;
        try {
            chatMessage = objectMapper.readValue(msg, ChatMessage.class);
        } catch(Exception ex) {
            return null;
        }
        if(chatMessage == null || (chatMessage!=null && chatMessage.getId()==null)) {
            return null;
        }

        return chatMessage;
    }

    public ChatRoom tryRoom(String msg) {
        ChatRoom chatRoom = null;
        try {
            chatRoom = objectMapper.readValue(msg, ChatRoom.class);
        } catch (Exception ex) {
            return null;
        }
        if(chatRoom == null || (chatRoom!=null && chatRoom.getId()==null)) {
            return null;
        }

        return chatRoom;
    }

    public void recvMsg(ChatMessage chatMessage) {
        /** SimpMessageSendingOperations 이용해서 Subscriber에게 메시지 보내기**/
        /** URL : '"/sub/chat/room/"+chatMessage.getRoomId()' 으로 chatMessage 보내기 **/
        messageTemplate.convertAndSend("/sub/chat/room/"+chatMessage.getRoomId(), chatMessage);
    }

    public void recvRoom(ChatRoom chatRoom) {
        System.out.println("여긴오냐?");
        messageTemplate.convertAndSend("/sub/chat/rooms", chatRoom);
    }

    public void recvReadDto(ReadDto readDto) {
        System.out.println("여기를 와야하는데??");
        messageTemplate.convertAndSend("/sub/chat/entrance", readDto);
    }
}
