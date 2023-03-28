package com.example.chattest.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class ChatWebSocketConfig implements WebSocketMessageBrokerConfigurer {

    /**
     * STOMP 관련 설정
     * configureMessageBroker에서 Application 내부에서 사용할 path를 지정 할 수 있다.
     */
    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        /** client에서 SEND 처리 **/
        registry.setApplicationDestinationPrefixes("/pub");

        /** 구독자들에게 전달 **/
        registry.enableSimpleBroker("/sub");
    }


    /**
     *  통신 endpoint 지정
     */
    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/chat").setAllowedOriginPatterns("*").withSockJS();
    }
}
