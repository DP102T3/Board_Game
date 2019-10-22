package com.example.boardgame.chat;

import java.io.Serializable;

public class Msg implements Serializable {

    public static final int CONTENT_TYPE_MSG = 0;
    public static final int CONTENT_TYPE_PIC = 1;
    public static final int TYPE_PLAYER_SEND = 0;
    public static final int TYPE_RECEIVED = 1;

    private String playerName;
    private String content;
    private int contentType;
    private int type;

    public Msg(String playerName, String content, int contentType, int type) {
        this.playerName = playerName;
        this.content = content;
        this.contentType = contentType;
        this.type = type;
    }

    public String getPlayerName() {
        return playerName;
    }

    public void setPlayerName(String playerName) {
        this.playerName = playerName;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getContentType() {
        return contentType;
    }

    public void setContentType(int contentType) {
        this.contentType = contentType;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}