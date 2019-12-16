package com.example.boardgame.shop;

import java.io.Serializable;

public class Game implements Serializable {

    private int gameNo ;
    private String gameName, gameType, gameNp, gameTime, gameIntro;

    public Game(int gameNo, String gameName, String gameType, String gameNp, String gameTime, String gameIntro) {
        this.gameNo = gameNo;
        this.gameName = gameName;
        this.gameType = gameType;
        this.gameNp = gameNp;
        this.gameTime = gameTime;
        this.gameIntro = gameIntro;
    }

    public Game(int gameNo, String gameName, String gameType) {
        this.gameNo = gameNo;
        this.gameName = gameName;
        this.gameType = gameType;
    }

    public int getGameNo() {
        return gameNo;
    }

    public void setGameNo(int gameNo) {
        this.gameNo = gameNo;
    }

    public String getGameName() {
        return gameName;
    }

    public void setGameName(String gamName) {
        this.gameName = gamName;
    }

    public String getGameType() {
        return gameType;
    }

    public void setGameType(String gameType) {
        this.gameType = gameType;
    }

    public String getGameNp() {
        return gameNp;
    }

    public void setGameNp(String gameNp) {
        this.gameNp = gameNp;
    }

    public String getGameTime() {
        return gameTime;
    }

    public void setGameTime(String gameTime) {
        this.gameTime = gameTime;
    }

    public String getGameIntro() {
        return gameIntro;
    }

    public void setGameIntro(String gameIntro) {
        this.gameIntro = gameIntro;
    }
}
