package org.zerock.workoutproject.exercise.controller;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Controller
public class SignalController {

    private static final Logger logger = LoggerFactory.getLogger(SignalController.class);
    private final SimpMessagingTemplate messagingTemplate;

    public SignalController(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
        logger.info("SignalController initialized with messaging template");
    }

    @MessageMapping("/signal/{roomId}")
    public void signal(@DestinationVariable String roomId, @Payload String message) {
        logger.info("WebSocket message received for room {}: {}", roomId, message);
        logger.debug("Processing signal for room {}, message length: {}", roomId, message.length());
        
        try {
            // 메시지 전송 경로 로깅
            String destination = "/topic/room/" + roomId;
            logger.info("Forwarding message to destination: {}", destination);
            
            // 바로 메시지 브로드캐스트
            messagingTemplate.convertAndSend(destination, message);
            logger.info("Message successfully forwarded to room {}", roomId);
        } catch (Exception e) {
            logger.error("Error forwarding message: {}", e.getMessage(), e);
            // 스택 트레이스 전체 로깅
            logger.error("Full stack trace:", e);
        }
    }
} 