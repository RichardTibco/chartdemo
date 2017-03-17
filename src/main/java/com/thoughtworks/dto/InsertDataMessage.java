package com.thoughtworks.dto;

/**
 * Created by pyang on 15/03/2017.
 */
public class InsertDataMessage {

    private String counter;

    private String createdDate;

    public InsertDataMessage() {
    }

    public InsertDataMessage(String counter, String createdDate) {
        this.counter = counter;
        this.createdDate = createdDate;
    }

    public String getCounter() {
        return counter;
    }

    public void setCounter(String counter) {
        this.counter = counter;
    }

    public String getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(String createdDate) {
        this.createdDate = createdDate;
    }
}
