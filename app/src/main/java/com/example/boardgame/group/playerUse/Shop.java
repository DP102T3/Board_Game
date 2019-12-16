package com.example.boardgame.group.playerUse;

public class Shop {
    private  int shopId;
    private String shopName;


    public Shop(int shopId, String shopName) {
        super();
        this.shopId = shopId;
        this.shopName = shopName;

    }

    public int getShopId() {
        return shopId;
    }

    public void setShopId(int shopId) {
        this.shopId = shopId;
    }

    public String getShopName() {
        return shopName;
    }

    public void setShopName(String shopName) {
        this.shopName = shopName;
    }






}
