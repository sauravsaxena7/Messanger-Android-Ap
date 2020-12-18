package com.jlcsoftware.bloodbankcommunity.Models;

public class MessagesModels {


    public String message,type,from;

    public long time;



    public boolean seem;


    public MessagesModels() {
    }

    public MessagesModels(String message, String type, String from, long time, boolean seem) {
        this.message = message;
        this.type = type;
        this.from = from;
        this.time = time;
        this.seem = seem;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public boolean isSeem() {
        return seem;
    }

    public void setSeem(boolean seem) {
        this.seem = seem;
    }
}
