package com.example.chattest.repository;

import com.example.chattest.model.ChatMessage;
import com.example.chattest.service.SubscriberService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.stereotype.Repository;
import com.example.chattest.model.ChatRoom;

import javax.annotation.PostConstruct;
import java.util.*;

@Repository
@RequiredArgsConstructor
public class ChatRepository {
    /** 채팅방(Topic)에 발행되는 메시지를 처리 **/
    private final RedisMessageListenerContainer redisMessageListener;

    private final SubscriberService subscriberService;

    /** Redis 로직 처리 **/
    private static final String ROOMS = "Rooms";
    private final RedisTemplate redisTemplate;

    private static final String MESSAGE = "Messages";

    private ListOperations<String,ChatMessage> opsListChatMessages;

    private HashOperations<String,String, ChatRoom> opsHashChatRoom;

    /**
     * 채팅방의 대화 메시지를 발행하기 위한 redis topic 정보.
     * 서버별로 채팅방에 매치되는 topic정보를 Map에 넣어 roomId로 찾을수 있도록 한다.
     * TODO: enterRoom 따로 빼야함!!
     */
    private Map<String, ChannelTopic> msgTopics = new HashMap<>();
    private Map<String, ChannelTopic> roomsTopics = new HashMap<>();

    /** @PostConstruct : 의존성 주입이 이루어진 후 초기화를 수행 **/
    @PostConstruct
    private void init(){
        /** redisTemplate에서 operation 받기 **/
        opsHashChatRoom = redisTemplate.opsForHash();
        opsListChatMessages = redisTemplate.opsForList();
        ChannelTopic topic = new ChannelTopic(ROOMS);
        roomsTopics.put(ROOMS, topic);
        redisMessageListener.addMessageListener(subscriberService, topic);
    }

    /**
     * 채팅방 생성
     */
    public String createChatRoom(Long user1, Long user2){
//        String roomId = UUID.randomUUID().toString();
//        Set<Long> members = new HashSet<>();
//        members.add(user1);
//        members.add(user2);
//        ChatRoom chatRoom = new ChatRoom(roomId, members);
//
//        /**
//         * Rooms 을 key로하는 hashmap에
//         * chatRoom 객체의 room id를 key로
//         * chatRoom 객체를 value로 저장 **/
//        opsHashChatRoom.put(ROOMS, chatRoom.getId(),chatRoom);
//
//        /*
//        채널 등록
//        채널: pub/sub에서 메세지를 전달하는 채널
//        얘를 사용해서 채팅방 간 메세지 전달
//         */
//        ChannelTopic topic = new ChannelTopic(roomId);
//
//        msgTopics.put(roomId, topic);
//        redisMessageListener.addMessageListener(subscriberService, topic);
//        return chatRoom.getId();
        return null;
    }

    /**
     * 채팅 방 목록(topics)에서 roomId에 해당하는 채팅방 이름 넘기기
     */
    public ChannelTopic getMsgTopic(String roomId){
        return msgTopics.get(roomId);
    }

    public ChannelTopic getRoomsTopic() {
        return roomsTopics.get(ROOMS);
    }

    /** hashmap을 다루는 key에 해당하는 value값 모두 가져오기
     * Rooms을 key로하는 hashmap에
     * 모든 value(chatRoom 객체) 가져오기
     */
    public List<ChatRoom> findAllRoom() {
        return opsHashChatRoom.values(ROOMS);
    }

    /** 값 가져오기
     * Rooms 을 key로하는 hashmap에
     * roomId를 key로 하는 value(chatRoom 객체) 가져오기
     */
    public ChatRoom findRoomById(String id) {
        return opsHashChatRoom.get(ROOMS, id);
    }

    public List<ChatMessage> findByRoomId(String id) {
        List<ChatMessage> messages = opsListChatMessages.range(id, 0, -1);
//        List<ChatMessage> result = new ArrayList<>();
//        messages.forEach(message -> {
//            if(message.getRoomId().equals(id)) {
//                result.add(message);
//            }
//        });
        return messages;
    }
}
