package com.example.boardgame.notification.UserAlarm;

public class SystemNotification {
    private int target,shop_id,note_state;
    private String user,bnote_title,bnote_content;
    private long setup_time;

    public SystemNotification(int target, int shop_id, int note_state, String user, String bnote_title,
                              String bnote_content, long setup_time) {
        super();
        this.target = target;
        this.shop_id = shop_id;
        this.note_state = note_state;
        this.user = user;
        this.bnote_title = bnote_title;
        this.bnote_content = bnote_content;
        this.setup_time = setup_time;
    }

    public int getTarget() {
        return target;
    }

    public void setTarget(int target) {
        this.target = target;
    }

    public int getShop_id() {
        return shop_id;
    }

    public void setShop_id(int shop_id) {
        this.shop_id = shop_id;
    }

    public int getNote_state() {
        return note_state;
    }

    public void setNote_state(int note_state) {
        this.note_state = note_state;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getBnote_title() {
        return bnote_title;
    }

    public void setBnote_title(String bnote_title) {
        this.bnote_title = bnote_title;
    }

    public String getBnote_content() {
        return bnote_content;
    }

    public void setBnote_content(String bnote_content) {
        this.bnote_content = bnote_content;
    }

    public long getSetup_time() {
        return setup_time;
    }

    public void setSetup_time(long setup_time) {
        this.setup_time = setup_time;
    }
}


