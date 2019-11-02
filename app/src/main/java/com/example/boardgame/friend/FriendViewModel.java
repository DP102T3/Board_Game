package com.example.boardgame.friend;

public class FriendViewModel {
    private String frNkName;
    private String frPic;
    private String frMood;
    private String frID;

    public FriendViewModel(String frNkName, String frPic, String frMood, String frID) {
        this.frNkName = frNkName;
        this.frPic = frPic;
        this.frMood = frMood;
        this.frID = frID;
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
}
