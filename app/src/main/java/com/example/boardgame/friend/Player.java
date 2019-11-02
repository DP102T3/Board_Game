package com.example.boardgame.friend;

public class Player {
    private String playerId;
    private String playerNkname;
    private int playerGender;
    private String playerPic1;
    private String playerPic2;
    private String playerPic3;
    private String playerPic4;
    private String playerPic5;
    private String playerState;
    private String playerBday;
    private String playerStar;
    private String playerMood;
    private String playerArea;
    private String playerFavBg;
    private String playerAdeptBg;
    private String playerIntro;

    public Player(String playerId, String playerNkname, int playerGender, String playerPic1,
                  String playerPic2, String playerPic3, String playerPic4, String playerPic5,
                  String playerState, String playerBday, String playerStar, String playerMood,
                  String playerArea, String playerFavBg, String playerAdeptBg, String playerIntro) {
        this.playerId = playerId;
        this.playerNkname = playerNkname;
        this.playerGender = playerGender;
        this.playerPic1 = playerPic1;
        this.playerPic2 = playerPic2;
        this.playerPic3 = playerPic3;
        this.playerPic4 = playerPic4;
        this.playerPic5 = playerPic5;
        this.playerState = playerState;
        this.playerBday = playerBday;
        this.playerStar = playerStar;
        this.playerMood = playerMood;
        this.playerArea = playerArea;
        this.playerFavBg = playerFavBg;
        this.playerAdeptBg = playerAdeptBg;
        this.playerIntro = playerIntro;
    }

    public String getPlayerId() {
        return playerId;
    }

    public void setPlayerId(String playerId) {
        this.playerId = playerId;
    }

    public String getPlayerNkname() {
        return playerNkname;
    }

    public void setPlayerNkname(String playerNkname) {
        this.playerNkname = playerNkname;
    }

    public int getPlayerGender() {
        return playerGender;
    }

    public void setPlayerGender(int playerGender) {
        this.playerGender = playerGender;
    }

    public String getPlayerPic1() {
        return playerPic1;
    }

    public void setPlayerPic1(String playerPic1) {
        this.playerPic1 = playerPic1;
    }

    public String getPlayerPic2() {
        return playerPic2;
    }

    public void setPlayerPic2(String playerPic2) {
        this.playerPic2 = playerPic2;
    }

    public String getPlayerPic3() {
        return playerPic3;
    }

    public void setPlayerPic3(String playerPic3) {
        this.playerPic3 = playerPic3;
    }

    public String getPlayerPic4() {
        return playerPic4;
    }

    public void setPlayerPic4(String playerPic4) {
        this.playerPic4 = playerPic4;
    }

    public String getPlayerPic5() {
        return playerPic5;
    }

    public void setPlayerPic5(String playerPic5) {
        this.playerPic5 = playerPic5;
    }

    public String getPlayerState() {
        return playerState;
    }

    public void setPlayerState(String playerState) {
        this.playerState = playerState;
    }

    public String getPlayerBday() {
        return playerBday;
    }

    public void setPlayerBday(String playerBday) {
        this.playerBday = playerBday;
    }

    public String getPlayerStar() {
        return playerStar;
    }

    public void setPlayerStar(String playerStar) {
        this.playerStar = playerStar;
    }

    public String getPlayerMood() {
        return playerMood;
    }

    public void setPlayerMood(String playerMood) {
        this.playerMood = playerMood;
    }

    public String getPlayerArea() {
        return playerArea;
    }

    public void setPlayerArea(String playerArea) {
        this.playerArea = playerArea;
    }

    public String getPlayerFavBg() {
        return playerFavBg;
    }

    public void setPlayerFavBg(String playerFavBg) {
        this.playerFavBg = playerFavBg;
    }

    public String getPlayerAdeptBg() {
        return playerAdeptBg;
    }

    public void setPlayerAdeptBg(String playerAdeptBg) {
        this.playerAdeptBg = playerAdeptBg;
    }

    public String getPlayerIntro() {
        return playerIntro;
    }

    public void setPlayerIntro(String playerIntro) {
        this.playerIntro = playerIntro;
    }
}
