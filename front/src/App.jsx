import { useState, useEffect } from "react";
import axios from "axios";
import Chat from './Chat';
import Stomp from 'stompjs';
import SockJS from 'sockjs-client';

function App() {
  const [chatRooms, setChatRooms] = useState([]);
  const [showChat, setShowChat] = useState(false);

  const ws = new SockJS('http://localhost:8080/chat');
  const stompClient = Stomp.over(ws);

  useEffect(() => {
    connect();
    findAllRoom();
  }, []);

  const findAllRoom = () => {
    axios.get('http://localhost:8080/chat/rooms')
      .then(response => {
        setChatRooms(response.data);
      });
  };

  const createRoom = () => {
    // const params = new URLSearchParams();
    // params.append("user1", "user1");
    // params.append("user2", "user2");
    // axios.post('http://localhost:8080/chat/room', params)
    //   .then(response => {
    //     alert(`${response.data} 방 개설에 성공하였습니다.`);
    //   })
    //   .catch(response => {
    //     alert("채팅방 개설에 실패하였습니다.");
    //   });
    const ws = new SockJS('http://localhost:8080/chat');
    const stompClient = Stomp.over(ws);
    // const set = new Set([1, 2]); //user id 목록
    stompClient.connect({}, function(frame) {
      stompClient.send("/pub/chat/rooms", {}, JSON.stringify({userIds:[1, 2]}));
      stompClient.disconnect();
    });
  };

  const enterRoom = (roomId) => {
    const sender = prompt('대화명을 입력해 주세요.');
    if (sender !== "") {
      localStorage.setItem('wschat.sender', sender);
      localStorage.setItem('wschat.roomId', roomId);
      setShowChat(true);
    }
    else return;
  };

  const connect = () => {
    let reconnect = 0;
    stompClient.connect({}, function(frame) {
      stompClient.subscribe("/sub/chat/rooms", function(message) {
        console.log("오냥")
        const recv = JSON.parse(message.body);
        console.log(recv)
        recvRoom(recv);
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

  const recvRoom = (recv) => {
    console.log(recv)
    setChatRooms(chatRooms => [
      recv,
      ...chatRooms
    ]);
  };

  return (
    <div>
      {showChat ? <Chat /> : (
        <div>
          <h2>"user1"이 "user2"와 같이 채팅하는 방을 만들어용</h2>
          <button onClick={createRoom}>방 생성</button>
          <ul>
            {chatRooms.map((chatRoom) => (
              <li key={chatRoom.id}>
                <span>{chatRoom.id}</span>
                <button onClick={() => enterRoom(chatRoom.id)}>입장</button>
              </li>
            ))}
          </ul>
        </div>
      )}
    </div>
  );
}

export default App;
