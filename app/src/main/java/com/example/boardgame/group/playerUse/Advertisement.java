package com.example.boardgame.group.playerUse;

public class Advertisement {
    private int adNo;
    private int shopId;

    public Advertisement(int adNo, int shopId) {
        this.adNo = adNo;
        this.shopId = shopId;
    }

    public int getAdNo() {
        return adNo;
    }

    public void setAdNo(int adNo) {
        this.adNo = adNo;
    }

    public int getShopId() {
        return shopId;
    }

    public void setShopId(int shopId) {
        this.shopId = shopId;
    }
}
