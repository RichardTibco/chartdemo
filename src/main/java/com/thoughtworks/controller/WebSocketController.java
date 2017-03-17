package com.thoughtworks.controller;

import com.thoughtworks.dao.ComputerRepository;
import com.thoughtworks.dto.NotificationMessage;
import com.thoughtworks.dto.InsertDataMessage;
import com.thoughtworks.model.Computer;
import com.thoughtworks.rabbitmq.FanoutSender;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

import java.io.IOException;
import java.sql.Date;
import java.util.concurrent.TimeoutException;

/**
 * Created by pyang on 15/03/2017.
 */
@Controller
public class WebSocketController {


    @Autowired
    private ComputerRepository computerRepository;

    @Autowired
    private FanoutSender sender;

    @MessageMapping("/hello")
    @SendTo("/topic/greetings")
    public NotificationMessage insertNewData(InsertDataMessage message) throws InterruptedException, IOException, TimeoutException {
        Thread.sleep(1000);

        try {
            if (message.getCounter() == null) {
                System.out.println("count");
            }

            if (message.getCreatedDate() == null) {
                System.out.println("date");
            }
            if (message.getCounter() != null && message.getCreatedDate() != null) {
                Integer count = Integer.valueOf(message.getCounter());
                Date date = Date.valueOf(message.getCreatedDate());

                Computer computer = new Computer();
                computer.setCount(count);
                computer.setCreated(date);
                System.out.println(count);
                System.out.println(date);
                computerRepository.save(computer);
            } else {
                System.out.println("Null!!!");
            }
        } catch (Exception e) {
            System.out.println("Error!!!");
        }

        sender.send();

        return new NotificationMessage("Counter is: " + message.getCounter() + ", created at: " + message.getCreatedDate());
    }
}
