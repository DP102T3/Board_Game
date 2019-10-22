package com.example.boardgame.chat.chat_group;

public class Group {
    private int groupNo;
    private String groupName;

    public Group(int groupNo, String groupName) {
        this.groupNo = groupNo;
        this.groupName = groupName;
    }

    public int getGroupNo() {
        return groupNo;
    }

    public void setGroupNo(int groupNo) {
        this.groupNo = groupNo;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }
}
