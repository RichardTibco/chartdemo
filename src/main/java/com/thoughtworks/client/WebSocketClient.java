package com.thoughtworks.client;

import javax.websocket.ContainerProvider;
import javax.websocket.DeploymentException;
import javax.websocket.Session;
import javax.websocket.WebSocketContainer;
import java.io.IOException;
import java.net.URI;

/**
 * Created by pyang on 17/03/2017.
 */
public class WebSocketClient {

    private Session session;

    protected void start()
    {

        WebSocketContainer container = ContainerProvider.getWebSocketContainer();

        String uri = "ws://localhost:8080/gs-guide-websocket/064/uxhqx1kv/websocket";
        System.out.println("Connecting to " + uri);
        try {
            session = container.connectToServer(WebSocketCB.class, URI.create(uri));
        } catch (DeploymentException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

//    public static void main(String args[]){
//        WebSocketClient client = new WebSocketClient();
//        client.start();
//
//        try {
//            client.session.getBasicRemote().sendText("null");
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//
//    }
}
