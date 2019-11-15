package com.example.boardgame.advertisement_points;

public class Advertisement {

    private int adNo;
    private int shopId;
    private String adPic;
    private String adStart;
    private String adBuyTime;
    private int adBuyDate;
    private int adAmount;
    private int adState;

    Advertisement(int adNo, int shopId, String adStart, int adBuyDate, String adBuyTime, int adAmount, int adState, String adPic) {
        this.adNo = adNo ;
        this.shopId = shopId;
        this.adStart = adStart;
        this.adBuyDate = adBuyDate;
        this.adBuyTime = adBuyTime;
        this.adAmount = adAmount;
        this.adState = adState;
        this.adPic = adPic;
    }

    public int getShopId() {
        return shopId;
    }
    public void setShopId(int shopId) {
        this.shopId = shopId;
    }
    public String getAdPic() {
        return adPic;
    }
    public void setAdPic(String adPic) {
        this.adPic = adPic;
    }
    public String getAdStart() {
        return adStart;
    }
    public void setAdStart(String adStart) {
        this.adStart = adStart;
    }
    public int getAdBuyDate() {
        return adBuyDate;
    }
    public void setAdBuyDate(int adBuyDate) {
        this.adBuyDate = adBuyDate;
    }
    public int getAdAmount() {
        return adAmount;
    }
    public void setAdAmount(int adAmount) {
        this.adAmount = adAmount;
    }
    public int getAdState() {
        return adState;
    }
    public void setAdState(int adState) {
        this.adState = adState;
    }

    public String getAdBuyTime() {
        return adBuyTime;
    }

    public void setAdBuyTime(String adBuyTime) {
        this.adBuyTime = adBuyTime;
    }

    public int getAdNo() {
        return adNo;
    }

    public void setAdNo(int adNo) {
        this.adNo = adNo;
    }

    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer() ;
        sb.append("") ;
        sb.append("adNo:"+adNo) ;
        sb.append(", shopId:"+shopId) ;
        sb.append(", adBuyDate:"+adBuyDate) ;
        sb.append(", adBuyTime:"+adBuyTime) ;
        sb.append("") ;
        return sb.toString() ;
    }

}