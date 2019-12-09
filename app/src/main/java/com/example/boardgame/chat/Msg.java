package com.example.boardgame.chat;

import java.io.Serializable;

public class Msg implements Serializable {

    public static final int CONTENT_TYPE_MSG = 0;
    public static final int CONTENT_TYPE_PIC = 1;
    public static final int TYPE_PLAYER_SEND = 0;
    public static final int TYPE_RECEIVED = 1;

    private String playerId;
    private String playerName;
    private String content;
    private int contentType;
    private int type;

    public Msg(String playerId, String playerName, String content, int contentType, int type) {
        this.playerId = playerId;
        this.playerName = playerName;
        this.content = content;
        this.contentType = contentType;
        this.type = type;
    }

    public String getPlayerId() {
        return playerId;
    }

    public void setPlayerId(String playerId) {
        this.playerId = playerId;
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