package com.example.boardgame.group.playerUse;

public class Member {
    private String playerId;
    private String playerNKName;

    public Member(String playerId, String playerNKName) {
        this.playerId = playerId;
        this.playerNKName = playerNKName;
    }

    public String getPlayerId() {
        return playerId;
    }

    public void setPlayerId(String playerId) {
        this.playerId = playerId;
    }

    public String getPlayerNKName() {
        return playerNKName;
    }

    public void setPlayerNKName(String playerNKName) {
        this.playerNKName = playerNKName;
    }
}
