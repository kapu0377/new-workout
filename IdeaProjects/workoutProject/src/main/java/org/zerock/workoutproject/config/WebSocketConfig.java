package org.zerock.workoutproject.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketTransportRegistration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    private static final Logger logger = LoggerFactory.getLogger(WebSocketConfig.class);

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        logger.info("Registering STOMP endpoints");
        registry.addEndpoint("/ws")
                .setAllowedOriginPatterns("*")
                .withSockJS()
                .setStreamBytesLimit(512 * 1024)
                .setHttpMessageCacheSize(3000)
                .setDisconnectDelay(30 * 1000);
        
        registry.addEndpoint("/ws")
                .setAllowedOriginPatterns("*");
                
        logger.info("STOMP endpoints registered successfully");
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        logger.info("Configuring message broker");
        registry.enableSimpleBroker("/topic")
                .setHeartbeatValue(new long[]{10000, 10000}) // 하트비트 간격 설정 (10초)
                .setTaskScheduler(heartBeatScheduler());
        // 메시지 전송 채널 접두사 설정 (클라이언트 -> 서버)
        registry.setApplicationDestinationPrefixes("/app");
        logger.info("Message broker configured successfully with prefix: /app and broker: /topic");
    }
    
    @Override
    public void configureWebSocketTransport(WebSocketTransportRegistration registration) {
        logger.info("Configuring WebSocket transport settings");
        registration.setMessageSizeLimit(512 * 1024)
                    .setSendBufferSizeLimit(1024 * 1024)
                    .setSendTimeLimit(30 * 1000);
        logger.info("WebSocket transport settings configured with increased limits");
    }
    
    @Bean
    public TaskScheduler heartBeatScheduler() {
        ThreadPoolTaskScheduler taskScheduler = new ThreadPoolTaskScheduler();
        taskScheduler.setPoolSize(2);
        taskScheduler.setThreadNamePrefix("ws-heartbeat-thread-");
        return taskScheduler;
    }
} 