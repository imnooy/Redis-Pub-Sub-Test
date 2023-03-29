import React, { useEffect, useState } from 'react';
import axios from 'axios';
import Stomp from 'stompjs';
import SockJS from 'sockjs-client';

function Chat() {
  const [roomId, setRoomId] = useState(localStorage.getItem('wschat.roomId'));
  const [sender, setSender] = useState(localStorage.getItem('wschat.sender'));
  const [room, setRoom] = useState({});
  const [message, setMessage] = useState('');
  const [messages, setMessages] = useState([]);

  const ws = new SockJS('http://localhost:8080/api/chat');
  const stompClient = Stomp.over(ws);

  useEffect(() => {
    findRoom();
    connect();
    axios.get(`http://localhost:8080/api/chat/room/${roomId}`+"/messages")
    .then((response) => {
      setMessages(response.data);
    })
    .catch((error) => {
      console.log(error);
    });
  }, []);

  const findRoom = () => {
    axios.get(`http://localhost:8080/api/chat/room/${roomId}`).then((response) => {
      setRoom(response.data);
    });
  };

  const sendMessage = () => {
    const ws = new SockJS('http://localhost:8080/api/chat');
    const stompClient = Stomp.over(ws);
    stompClient.connect({}, function(frame) {
      stompClient.send("/api/pub/chat/message", {}, JSON.stringify({roomId:roomId, senderId:sender, content:message}));
      setMessage('');
      stompClient.disconnect();
    });
  };

  const recvMessage = (recv) => {
    console.log("받음?")
    console.log(recv)
    setMessages(messages => [
      ...messages,
      recv
    ]);
  };

  const connect = () => {
    let reconnect = 0;
    stompClient.connect({}, function(frame) {
      stompClient.subscribe("/sub/chat/entrance", function(readDto) {
        const readIndex = JSON.parse(readDto.body);
        recvReadDto(readIndex);
      })
      
      stompClient.subscribe(`/sub/chat/room/${roomId}`, function(message) {
        const recv = JSON.parse(message.body);
        recvMessage(recv);
      });
    }, function(error) {
      // if(reconnect++ < 5) {
      //   setTimeout(function() {
      //     console.log("connection reconnect");
      //     connect();
      //   },10*1000);
      // }
    });
  };

  const recvReadDto = (readIndex) => {
    const lastReadIndex = readIndex.lastReadIndex;
    const tempmessage = [...messages];
    for(let i = lastReadIndex ; i < tempmessage.length ; i++){
      tempmessage[i].isRead = true;
    }

    setMessages([
      ...tempmessage,
    ]);
  }

  return (
    <div id="Chat">
      <div>{room.id}</div>
      <ul>
        {messages.map((msg, index) => (
          <li key={index}>
            <span>{msg.senderId}가 보냄: </span>
            <span>{msg.content}</span>
            <h3>{msg.isRead ? '읽음' : '안읽음'}</h3>
          </li>
        ))}
      </ul>
      <input type="text" value={message} onChange={(e) => setMessage(e.target.value)} />
      <button onClick={sendMessage}>Send</button>
    </div>
  );
}

export default Chat;

