package com.thoughtworks.client;

import javax.websocket.*;
import java.io.IOException;

/**
 * Created by pyang on 17/03/2017.
 */
@ClientEndpoint
public class WebSocketCB {

    @OnOpen
    public void onOpen(Session session) {
        System.out.println("Connected to endpoint: " + session.getBasicRemote());
        try {
            session.getBasicRemote().sendText("null");
        } catch (IOException ex) {
        }
    }

    @OnMessage
    public void onMessage(String message) {
        System.out.println(message);
    }

    @OnError
    public void onError(Throwable t) {
        t.printStackTrace();
    }
}
