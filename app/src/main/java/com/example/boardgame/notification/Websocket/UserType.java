package com.example.boardgame.Notification.Websocket;

public class UserType {
    String type,userId;

    public UserType(String type, String userId) {
        this.type = type;
        this.userId = userId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
