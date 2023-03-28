package com.example.chattest.repository;

import com.example.chattest.model.ChatRoom;
import org.springframework.data.repository.CrudRepository;

public interface ChatRoomRepository extends CrudRepository<ChatRoom, String> {

}
