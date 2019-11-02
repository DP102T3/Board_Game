package com.example.boardgame.friend;

import java.util.List;

public class FriendListResult {

    private List<Friend> result;

    public List<Friend> getResult() {
        return result;
    }

    public void setResult(List<Friend> result) {
        this.result = result;
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();

        int i = 1;

        for (Friend friend: result) {
            stringBuilder.append("Object" + i + " - ").append(friend.toString()).append("\n");
            i++;
        }

        return stringBuilder.toString();
    }
}
