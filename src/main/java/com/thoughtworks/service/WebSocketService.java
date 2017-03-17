package com.thoughtworks.service;

import com.thoughtworks.dto.NotificationMessage;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * Created by pyang on 16/03/2017.
 */
@Service
public class WebSocketService {
    @Resource
    private SimpMessagingTemplate simpMessagingTemplate;

    public void notifyWebSocketClient() {

        simpMessagingTemplate.convertAndSend("/topic/greetings", "test");
    }
}
