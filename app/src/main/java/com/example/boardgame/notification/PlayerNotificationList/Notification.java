package com.example.boardgame.notification.PlayerNotificationList;

import java.io.Serializable;

public class Notification implements Serializable {
    private int pnote_no,pnote_state;
    private String player_id,pnote_title,pnote_info,setup_time;



    public Notification(int pnote_no, String player_id, String pnote_title, String pnote_info, String setup_time, int pnote_state) {
        this.pnote_no = pnote_no;
        this.player_id = player_id;
        this.pnote_title = pnote_title;
        this.pnote_info = pnote_info;
        this.setup_time = setup_time;
        this.pnote_state = pnote_state;
    }

    public int getPnote_no() {
        return pnote_no;
    }

    public void setPnote_no(int pnote_no) {
        this.pnote_no = pnote_no;
    }

    public String getPlayer_id() {
        return player_id;
    }

    public void setPlayer_id(String player_id) {
        this.player_id = player_id;
    }

    public String getPnote_title() {
        return pnote_title;
    }

    public void setPnote_title(String pnote_title) {
        this.pnote_title = pnote_title;
    }

    public String getPnote_info() {
        return pnote_info;
    }

    public void setPnote_info(String pnote_info) {
        this.pnote_info = pnote_info;
    }

    public String getSetup_time() {
        return setup_time;
    }

    public void setSetup_time(String setup_time) {
        this.setup_time = setup_time;
    }

    public int getPnote_state() {
        return pnote_state;
    }

    public void setPnote_state(int pnote_state) {
        this.pnote_state = pnote_state;
    }
}