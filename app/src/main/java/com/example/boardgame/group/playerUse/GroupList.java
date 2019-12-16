package com.example.boardgame.group.playerUse;

import java.io.Serializable;

public class GroupList implements Serializable {
    private int id;
    private String groupName;
    private String groupArea;
    private String groupDate;

    public GroupList(int id, String groupName, String groupArea, String groupDate) {

        this.id = id;
        this.groupName = groupName;
        this.groupArea = groupArea;
        this.groupDate = groupDate;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public String getGroupArea() {
        return groupArea;
    }

    public void setGroupArea(String groupArea) {
        this.groupArea = groupArea;
    }

    public String getGroupDate() {
        return groupDate;
    }

    public void setGroupDate(String groupDate) {
        this.groupDate = groupDate;
    }
}
