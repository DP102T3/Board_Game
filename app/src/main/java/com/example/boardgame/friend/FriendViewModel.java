package com.example.boardgame.friend;

public class FriendViewModel {
    private String frNkName;
    private String frPic;
    private String frMood;
    private String frID;
    private int pointCount;

    public FriendViewModel(String frNkName, String frPic, String frMood, String frID) {
        this.frNkName = frNkName;
        this.frPic = frPic;
        this.frMood = frMood;
        this.frID = frID;
    }

    public FriendViewModel(String frID, String frNkName, String frPic) {
        this.frNkName = frNkName;
        this.frPic = frPic;
        this.frID = frID;
    }

    public FriendViewModel(String frNkName, String frPic, String frMood, String frID, int pointCount) {
        this.frNkName = frNkName;
        this.frPic = frPic;
        this.frMood = frMood;
        this.frID = frID;
        this.pointCount = pointCount;
    }

    public String getFrID() {
        return frID;
    }

    public void setFrID(String frID) {
        this.frID = frID;
    }

    public String getFrNkName() {
        return frNkName;
    }

    public void setFrNkName(String frNkName) {
        this.frNkName = frNkName;
    }

    public String getFrPic() {
        return frPic;
    }

    public void setFrPic(String frPic) {
        this.frPic = frPic;
    }

    public String getFrMood() {
        return frMood;
    }

    public void setFrMood(String frMood) {
        this.frMood = frMood;
    }


    public int getPointCount() {
        return pointCount;
    }

    public void setPointCount(int pointCount) {
        this.pointCount = pointCount;
    }
}
