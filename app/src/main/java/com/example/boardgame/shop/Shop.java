package com.example.boardgame.shop;

import java.io.Serializable;

public class Shop implements Serializable {

    private String shopPassword, shopName, shopAddress, shopMail, shopOwner, shopIntro ,gameName , timeOpen, timeClose,shopStatus, shopCharge;
    private int rateTotal;
    private int shopId, shopTel, rateCount;
    private byte[] image;




    public Shop(String shopPassword, String shopName, String shopAddress, String shopMail, String shopOwner, String shopIntro, String gameName, int shopId, int shopTel, String timeOpen, String timeClose, String shopCharge, byte[] image, int rateTotal, String shopStatus) {
        this.shopPassword = shopPassword;
        this.shopName = shopName;
        this.shopAddress = shopAddress;
        this.shopMail = shopMail;
        this.shopOwner = shopOwner;
        this.shopIntro = shopIntro;
        this.gameName = gameName;
        this.shopId = shopId;
        this.shopTel = shopTel;
        this.timeOpen = timeOpen;
        this.timeClose = timeClose;
        this.shopCharge = shopCharge;
        this.image = image;
        this.rateTotal = rateTotal;
        this.shopStatus = shopStatus;
    }



    public void setFields(int shopId, String shopAddress, String shopOwner, int shopTel, String shopCharge, String shopOpen, String shopClose, String shopIntro) {
        this.shopId = shopId;
        this.shopAddress = shopAddress;
        this.shopOwner = shopOwner;
        this.shopIntro = shopIntro;
        this.timeOpen = shopOpen;
        this.timeClose = shopClose;
        this.shopTel = shopTel;
        this.shopCharge = shopCharge;

    }


    public Shop(String shopAddress, int shopId, int shopTel, String shopCharge, byte[] image) {
        this.shopAddress = shopAddress;
        this.shopId = shopId;
        this.shopTel = shopTel;
        this.shopCharge = shopCharge;
        this.image = image;

    }




    public Shop() {
    }

    public Shop(String gameName) {
        this.gameName = gameName;
    }



    public Shop(String shopAddress, String shopIntro, int shopTel, String timeOpen, String timeClose, String shopCharge) {
        this.shopTel = shopTel;
        this.shopAddress = shopAddress;
        this.shopIntro = shopIntro;
        this.timeOpen = timeOpen;
        this.timeClose = timeClose;
        this.shopCharge = shopCharge;
    }


    public Shop(String shopName, int shopTel, String shopAddress, String mail, String shopOwner) {
        this.shopName = shopName;
        this.shopTel = shopTel;
        this.shopAddress = shopAddress;
        this.shopMail = mail;
        this.shopOwner = shopOwner;
    }

    public Shop(int shopId, String shopPassword) {
        this.shopId = shopId;
        this.shopPassword = shopPassword;

    }

    public Shop(int shopId, String shopName, String shopAddress, int rateTotal, int rateCount, String shopStatus) {
        super();
        this.shopId = shopId;
        this.shopName = shopName;
        this.shopAddress = shopAddress;
        this.rateTotal = rateTotal;
        this.rateCount = rateCount;
        this.shopStatus = shopStatus;
    }


    public int getShopId() {
        return shopId;
    }

    public void setShopId(int shopId) {
        this.shopId = shopId;
    }

    public String getShopPassword() {
        return shopPassword;
    }

    public void setShopPassword(String shopPassword) {
        this.shopPassword = shopPassword;
    }

    public String getShopName() {
        return shopName;
    }

    public void setShopName(String shopName) {
        this.shopName = shopName;
    }

    public int getShopTel() {
        return shopTel;
    }

    public void setShopTel(int shopTel) {
        this.shopTel = shopTel;
    }

    public String getShopAddress() {
        return shopAddress;
    }

    public void setShopAddress(String shopAddress) {
        this.shopAddress = shopAddress;
    }

    public String getShopMail() {
        return shopMail;
    }

    public void setShopMail(String mail) {
        this.shopMail = mail;
    }

    public String getShopOwner() {
        return shopOwner;
    }

    public void setShopOwner(String shopOwner) {
        this.shopOwner = shopOwner;
    }

    public byte[] getPic1() {
        return image;
    }

    public void setPic1(byte[] pic1) {
        this.image = image;
    }

    public Shop(byte[] pic1) {
        this.image = image;
    }

    public String getShopIntro() {
        return shopIntro;
    }

    public void setShopIntro(String shopIntro) {
        this.shopIntro = shopIntro;
    }

    public String getTimeOpen() {
        return timeOpen;
    }

    public void setTimeOpen(String timeOpen) {
        this.timeOpen = timeOpen;
    }

    public String getTimeClose() { return timeClose; }

    public void setTimeClose(String timeClose) { this.timeClose = timeClose; }


    public String getShopCharge() {
        return shopCharge;
    }

    public void setShopCharge(String shopCharge) { this.shopCharge = shopCharge; }

    public String getGameName() { return gameName; }

    public void setGameName(String gameName) { this.gameName = gameName; }


    public int getRateTotal() {
        return rateTotal;
    }

    public void setRateTotal(int rateTotal) {
        this.rateTotal = rateTotal;
    }

    public String getShopStatus() { return shopStatus; }

    public void setShopStatus(String shopStatus) { this.shopStatus = shopStatus; }

    public int getRateCount() {
        return rateCount;
    }

    public void setRateCount(int rateCount) {
        this.rateCount = rateCount;
    }
}
