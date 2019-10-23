package com.example.boardgame.notification;

import java.io.Serializable;

public class ShopNotification implements Serializable {
    private int shop_id;
    private String snote_title,snote_info,setup_time;
    private int snote_state;
    public ShopNotification(int shop_id, String snote_title, String snote_info, String setup_time, int snote_state) {
        super();
        this.shop_id = shop_id;
        this.snote_title = snote_title;
        this.snote_info = snote_info;
        this.setup_time = setup_time;
        this.snote_state = snote_state;
    }
    public int getShop_id() {
        return shop_id;
    }
    public void setShop_id(int shop_id) {
        this.shop_id = shop_id;
    }
    public String getSnote_title() {
        return snote_title;
    }
    public void setSnote_title(String snote_title) {
        this.snote_title = snote_title;
    }
    public String getSnote_info() {
        return snote_info;
    }
    public void setSnote_info(String snote_info) {
        this.snote_info = snote_info;
    }
    public String getSetup_time() {
        return setup_time;
    }
    public void setSetup_time(String setup_time) {
        this.setup_time = setup_time;
    }
    public int getSnote_state() {
        return snote_state;
    }
    public void setSnote_state(int snote_state) {
        this.snote_state = snote_state;
    }
}