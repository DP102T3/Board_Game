package com.example.boardgame.shop;

import java.io.Serializable;

public class Game implements Serializable {

    private int gameNo ;

    private String gamName, gameType;

    public Game(int gameNo, String gamName, String gameType) {
        this.gameNo = gameNo;
        this.gamName = gamName;
        this.gameType = gameType;
    }

    public int getGameNo() {
        return gameNo;
    }

    public void setGameNo(int gameNo) {
        this.gameNo = gameNo;
    }

    public String getGamName() {
        return gamName;
    }

    public void setGamName(String gamName) {
        this.gamName = gamName;
    }

    public String getGameType() {
        return gameType;
    }

    public void setGameType(String gameType) {
        this.gameType = gameType;
    }
}
