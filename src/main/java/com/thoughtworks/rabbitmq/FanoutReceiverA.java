package com.thoughtworks.rabbitmq;

import com.thoughtworks.service.WebSocketService;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@RabbitListener(queues = "fanout.B")
public class FanoutReceiverA {

    @Autowired
    WebSocketService webSocketService;

    @RabbitHandler
    public void process(String message) {
        System.out.println("fanout Receiver B  : " + message);
        webSocketService.notifyWebSocketClient();
    }

}
