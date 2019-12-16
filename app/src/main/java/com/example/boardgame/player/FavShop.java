package com.example.boardgame.player;

public class FavShop {
    private int shopId;
    private String shopName;
    private int rateCount;
    private int rateTotal;
    private String shopAddress;

    public FavShop(int shopId, String shopName, int rateCount, int rateTotal, String shopAddress) {
        this.shopId = shopId;
        this.shopName = shopName;
        this.rateCount = rateCount;
        this.rateTotal = rateTotal;
        this.shopAddress = shopAddress;
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

    public int getRateCount() {
        return rateCount;
    }

    public void setRateCount(int rateCount) {
        this.rateCount = rateCount;
    }

    public int getRateTotal() {
        return rateTotal;
    }

    public void setRateTotal(int rateTotal) {
        this.rateTotal = rateTotal;
    }

    public String getShopAddress() {
        return shopAddress;
    }

    public void setShopAddress(String shopAddress) {
        this.shopAddress = shopAddress;
    }
}
