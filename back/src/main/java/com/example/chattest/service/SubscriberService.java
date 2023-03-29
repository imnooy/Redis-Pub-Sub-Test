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
        ChatMessage msgObj = null;
        try {
            System.out.println("메세지임?");
            msgObj = objectMapper.readValue(publishedMessge, ChatMessage.class);
            if(msgObj != null && msgObj.getId() == null) {
                throw new Exception();
            }
            System.out.println(msgObj);
        } catch (Exception e) {
            System.out.println("hi");
            ChatRoom chatRoom = null;
            try {
                chatRoom = objectMapper.readValue(publishedMessge, ChatRoom.class);
                if(chatRoom == null || (chatRoom != null && chatRoom.getId() == null)) {
                    throw new Exception();
                }
                System.out.println(msgObj);
                recvRoom(chatRoom);
            } catch (Exception ex) {
                ReadDto readDto = null;
                System.out.println("얘도 호출좀.");
                try {
                    readDto = objectMapper.readValue(publishedMessge, ReadDto.class);
                    recvReadDto(readDto);
                } catch (JsonProcessingException e1) {
                    throw new RuntimeException(e1);
                }
            }
        }
        if(msgObj != null) {
            System.out.println("왜안와");
            /**deserialize 한 메시지를 내가 만든 ChatMessage 객체로 맵핑 **/
            recvMsg(msgObj);
        }

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
        messageTemplate.convertAndSend("/sub/chat/entrance", readDto);
    }
}
