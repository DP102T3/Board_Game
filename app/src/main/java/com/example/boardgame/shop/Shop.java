package com.example.boardgame.shop;

import java.io.Serializable;

public class Shop implements Serializable {

    private String shopPassword, shopName, shopAddress, shopMail, shopOwner, shopIntro ,gameName;

    private int shopId, shopTel, shopOpen, shopCharge;
    private double shopFristpic;

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

    public double getShopFristpic() {
        return shopFristpic;
    }

    public void setShopFristpic(double shopFristpic) {
        this.shopFristpic = shopFristpic;
    }

    public Shop(double shopFristpic) {
        this.shopFristpic = shopFristpic;
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

}
