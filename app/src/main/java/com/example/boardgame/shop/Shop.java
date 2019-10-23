package com.example.boardgame.shop;

import java.io.Serializable;

public class Shop implements Serializable {

    private String shopPassword, shopName, shopAddress, shopMail, shopOwner, shopIntro ,gameName;

    private int shopId, shopTel, shopOpen, shopClose, shopCharge;
    private boolean shopPic1,shopPic2;;


    public int getShopClose() {
        return shopClose;
    }

    public void setShopClose(int shopClose) {
        this.shopClose = shopClose;
    }

    public Shop(String shopPassword, String shopName, String shopAddress, String shopMail, String shopOwner, String shopIntro, String gameName, int shopId, int shopTel, int shopOpen, int shopClose, int shopCharge, boolean shopPic1, boolean  shopPic2) {
        this.shopPassword = shopPassword;
        this.shopName = shopName;
        this.shopAddress = shopAddress;
        this.shopMail = shopMail;
        this.shopOwner = shopOwner;
        this.shopIntro = shopIntro;
        this.gameName = gameName;
        this.shopId = shopId;
        this.shopTel = shopTel;
        this.shopOpen = shopOpen;
        this.shopClose = shopClose;
        this.shopCharge = shopCharge;
        this.shopPic1 = shopPic1;
        this.shopPic2 = shopPic2;
    }



    public Shop(String shopAddress, int shopId, int shopTel, int shopCharge, boolean shopPic1, boolean shopPic2) {
        this.shopAddress = shopAddress;
        this.shopId = shopId;
        this.shopTel = shopTel;
        this.shopCharge = shopCharge;
        this.shopPic1 = shopPic1;
        this.shopPic2 = shopPic2;
    }

    public void setShopPic2(boolean shopPic2) {
        this.shopPic2 = shopPic2;
    }



    public Shop() {
    }

    public Shop(String gameName) {
        this.gameName = gameName;
    }



    public Shop(String shopAddress, String shopIntro, int shopTel, int shopOpen, int shopCharge) {
        this.shopTel = shopTel;
        this.shopAddress = shopAddress;
        this.shopIntro = shopIntro;
        this.shopOpen = shopOpen;
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

    public boolean getShopPic1() {
        return shopPic1;
    }

    public void setShopPic1( boolean shopPic1) {
        this.shopPic1 = shopPic1;
    }

    public Shop(boolean shopPic1) {
        this.shopPic1 = shopPic1;
    }

    public String getShopIntro() {
        return shopIntro;
    }

    public void setShopIntro(String shopIntro) {
        this.shopIntro = shopIntro;
    }

    public int getShopOpen() {
        return shopOpen;
    }

    public void setShopOpen(int shopOpen) {
        this.shopOpen = shopOpen;
    }

    public int getShopCharge() {
        return shopCharge;
    }

    public void setShopCharge(int shopCharge) { this.shopCharge = shopCharge; }

    public String getGameName() { return gameName; }

    public void setGameName(String gameName) { this.gameName = gameName; }

    public boolean getShopPic2() {
        return shopPic2;
    }


}
