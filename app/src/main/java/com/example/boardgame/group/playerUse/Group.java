package com.example.boardgame.group.playerUse;

import java.io.Serializable;

public class Group implements Serializable {
    private String groupName;
    //private String areaLv2;
    //private String areaLv3;
    private int shopIdSelect;
    private String gameClass;
    private String gameName;
    private long groupStartDateTime;
    private long groupStopDateTime;
    private int peopleLeast;
    private int peopleMax;
    private int peopleCondition;
    private String budget;
    private long joinStopDateTime;
    private String otherDescription;
    private int id;
    private int peopleJoin;
    private String playerId;
    private String groupPlayerId;


    public Group(String groupName, long groupStartDateTime, int id) {
        this.groupName = groupName;
        this.groupStartDateTime = groupStartDateTime;
        this.id = id;
    }

    public Group(String groupName,  int shopIdSelect, String gameClass, String gameName, long groupStartDateTime, long groupStopDateTime, int peopleLeast, int peopleMax, int peopleCondition, String budget, long joinStopDateTime, String otherDescription,int peopleJoin,String playerId, String groupPlayerId) {
        this.groupName = groupName;
        this.shopIdSelect = shopIdSelect;
        this.gameClass = gameClass;
        this.gameName = gameName;
        this.groupStartDateTime = groupStartDateTime;
        this.groupStopDateTime = groupStopDateTime;
        this.peopleLeast = peopleLeast;
        this.peopleMax = peopleMax;
        this.peopleCondition = peopleCondition;
        this.budget = budget;
        this.joinStopDateTime = joinStopDateTime;
        this.otherDescription = otherDescription;
        this.peopleJoin=peopleJoin;
        this.playerId=playerId;
        this.groupPlayerId=groupPlayerId;
    }

    public Group(String groupName,  int shopIdSelect, String gameClass, String gameName, long groupStartDateTime, long groupStopDateTime, int peopleLeast, int peopleMax, int peopleCondition, String budget, long joinStopDateTime, String otherDescription,int peopleJoin,String playerId) {
        this.groupName = groupName;
        this.shopIdSelect = shopIdSelect;
        this.gameClass = gameClass;
        this.gameName = gameName;
        this.groupStartDateTime = groupStartDateTime;
        this.groupStopDateTime = groupStopDateTime;
        this.peopleLeast = peopleLeast;
        this.peopleMax = peopleMax;
        this.peopleCondition = peopleCondition;
        this.budget = budget;
        this.joinStopDateTime = joinStopDateTime;
        this.otherDescription = otherDescription;
        this.peopleJoin=peopleJoin;
        this.playerId=playerId;

    }

    public String getPlayerId(){
        return playerId;
    }

    public void setPlayerId(String playerId){
        this.playerId=playerId;
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

    public int getShopIdSelect() {
        return shopIdSelect;
    }

    public void setShopIdSelect(int shopIdSelect) {
        this.shopIdSelect = shopIdSelect;
    }

    public String getGameClass() {
        return gameClass;
    }

    public void setGameClass(String gameClass) {
        this.gameClass = gameClass;
    }

    public String getGameName() {
        return gameName;
    }

    public void setGameName(String gameName) {
        this.gameName = gameName;
    }

    public long getGroupStartDateTime() {
        return groupStartDateTime;
    }

    public void setGroupStartDateTime(long groupStartDateTime) {
        this.groupStartDateTime = groupStartDateTime;
    }

    public long getGroupStopDateTime() {
        return groupStopDateTime;
    }

    public void setGroupStopDateTime(long groupStopDateTime) {
        this.groupStopDateTime = groupStopDateTime;
    }

    public int getPeopleLeast() {
        return peopleLeast;
    }

    public void setPeopleLeast(int peopleLeast) {
        this.peopleLeast = peopleLeast;
    }

    public int getPeopleMax() {
        return peopleMax;
    }

    public void setPeopleMax(int peopleMax) {
        this.peopleMax = peopleMax;
    }

    public int getPeopleCondition() {
        return peopleCondition;
    }

    public void setPeopleCondition(int peopleCondition) {
        this.peopleCondition = peopleCondition;
    }

    public String getBudget() {
        return budget;
    }

    public void setBudget(String budget) {
        this.budget = budget;
    }

    public long getJoinStopDateTime() {
        return joinStopDateTime;
    }

    public void setJoinStopDateTime(long joinStopDateTime) {
        this.joinStopDateTime = joinStopDateTime;
    }

    public String getOtherDescription() {
        return otherDescription;
    }

    public void setOtherDescription(String otherDescription) {
        this.otherDescription = otherDescription;
    }

    public int getPeopleJoin(){
        return peopleJoin;
    }

    public void setPeopleJoin(int peopleJoin){
        this.peopleJoin=peopleJoin;
    }

    public String getGroupPlayerId(){
        return groupPlayerId;
    }

    public void setGroupPlayerId(String playerId){this.groupPlayerId=groupPlayerId;}

}
