package com.example.boardgame.chat.chat_friend;

import java.io.Serializable;

public class Friend implements Serializable {
    private String friendId;
    private String friendNkName;
    private int position = -1;
    public static int PLAYER_1 = 0;
    public static int PLAYER_2 = 1;

    public Friend(String friendId, String friendNkName) {
        this.friendId = friendId;
        this.friendNkName = friendNkName;
    }

    public Friend(String friendId, String friendNkName, int position) {
        this.friendId = friendId;
        this.friendNkName = friendNkName;
        this.position= position;
    }

    public String getFriendId() {
        return friendId;
    }

    public void setFriendId(String friendId) {
        this.friendId = friendId;
    }

    public String getFriendNkName() {
        return friendNkName;
    }

    public void setFriendNkName(String friendNkName) {
        this.friendNkName = friendNkName;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }
}
