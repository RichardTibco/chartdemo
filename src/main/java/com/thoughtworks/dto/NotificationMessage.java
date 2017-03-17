package com.thoughtworks.dto;

/**
 * Created by pyang on 15/03/2017.
 */
public class NotificationMessage {

    private String content;

    public NotificationMessage() {
    }

    public NotificationMessage(String content) {
        this.content = content;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
