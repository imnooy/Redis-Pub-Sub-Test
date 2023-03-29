import { useState, useEffect } from "react";
import axios from "axios";
import Chat from './Chat';
import Stomp from 'stompjs';
import SockJS from 'sockjs-client';

function App() {
  const [chatRooms, setChatRooms] = useState([]);
  const [showChat, setShowChat] = useState(false);

  const ws = new SockJS('http://localhost:8080/api/chat');
  const stompClient = Stomp.over(ws);

  useEffect(() => {
    connect();
    findAllRoom();
  }, []);

  const findAllRoom = () => {
    axios.get('http://localhost:8080/api/chat/rooms')
      .then(response => {
        setChatRooms(response.data);
      });
  };

  //방 만들기
  const createRoom = () => {
    /*
      소켓 연결
      /chat으로 가는 요청에 대해 소켓 연결 설정
    */
    const ws = new SockJS('http://localhost:8080/api/chat');
    const stompClient = Stomp.over(ws);

    /*
      /chat/rooms를 구독하고 있는 Subscriber들을 대상으로
      {userId: [1, 2]} 객체를 Publish 하겠다.
    */
    stompClient.connect({}, function(frame) {
      stompClient.send("/api/pub/chat/rooms", {}, JSON.stringify({userIds:[1, 2]}));
      // stompClient.disconnect();
    });
  };

  const enterRoom = (roomId) => {
    /*
      Test용 채팅방에서 쓸 user id 입력 - Long 타입으로
      TODO: 알림 보낸 상대의 user id만 객체로 묶어 보내주기
    */
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
    /*
      페이지 렌더링 후 실행되는 connect()
      /chat/rooms 라는 경로를 구독하겠다
      구독한 채널에서 Publish된 메세지가 왔을 때
      처리 (recvRoom())
    */
    stompClient.connect({}, function(frame) {
      stompClient.subscribe("/sub/chat/rooms", function(message) {
        const recv = JSON.parse(message.body);
        console.log(recv)
        recvRoom(recv);
      });
    }, function(error) {
      // 연결이 끊어졌을 때 재연결 시도 부분
      // 필요할 때 쓰면 될 듯.
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
