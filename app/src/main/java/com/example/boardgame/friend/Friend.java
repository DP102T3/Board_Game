package com.example.boardgame.friend;

public class Friend {
    private String player1Id;
    private String player2Id;
    private String player2Name;
    private String player2Pic;
    private int inviteStatus;
    private int pointCount;
    private String player2Mood;
    private String lastUpdateTime;

    public Friend(String player1Id, String player2Id, String player2Name, String player2Pic, int inviteStatus, int pointCount, String player2Mood) {
        this.player1Id = player1Id;
        this.player2Id = player2Id;
        this.player2Name = player2Name;
        this.player2Pic = player2Pic;
        this.inviteStatus = inviteStatus;
        this.pointCount = pointCount;
        this.player2Mood = player2Mood;
    }

    public Friend(String player1Id, String player2Id) {
        this.player1Id = player1Id;
        this.player2Id = player2Id;
    }

    public Friend(String player1Id, String player2Id, int pointCount, int inviteStatus) {
        this.player1Id = player1Id;
        this.player2Id = player2Id;
        this.pointCount = pointCount;
        this.inviteStatus = inviteStatus;
    }

    public String getPlayer1Id() {
        return player1Id;
    }

    public void setPlayer1Id(String player1Id) {
        this.player1Id = player1Id;
    }

    public String getPlayer2Id() {
        return player2Id;
    }

    public void setPlayer2Id(String player2Id) {
        this.player2Id = player2Id;
    }

    public int getInviteStatus() {
        return inviteStatus;
    }

    public void setInviteStatus(int inviteStatus) {
        this.inviteStatus = inviteStatus;
    }

    public int getPointCount() {
        return pointCount;
    }

    public void setPointCount(int pointCount) {
        this.pointCount = pointCount;
    }

    public String getLastUpdateTime() {
        return lastUpdateTime;
    }

    public void setLastUpdateTime(String lastUpdateTime) {
        this.lastUpdateTime = lastUpdateTime;
    }

    public String getPlayer2Pic() {
        return player2Pic;
    }

    public void setPlayer2Pic(String player2Pic) {
        this.player2Pic = player2Pic;
    }

    public String getPlayer2Name() {
        return player2Name;
    }

    public void setPlayer2Name(String player2Name) {
        this.player2Name = player2Name;
    }

    public String getPlayer2Mood() {
        return player2Mood;
    }

    public void setPlayer2Mood(String player2Mood) {
        this.player2Mood = player2Mood;
    }
    }
