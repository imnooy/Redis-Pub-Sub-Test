package com.example.chattest.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Data
@RedisHash("Rooms")
public class ChatRoom implements Serializable{
    @Id
    private String id; //채팅방 ID
    private Set<Long> userIds; //참여자 목록
    private Map<Long, Long> lastReadIndex; //참여자들이 마지막으로 읽은 메시지 인덱스
    private Long msgs; //채팅방의 총 메세지 개수
    private String content; //채팅방의 마지막 메세지 내용
}
