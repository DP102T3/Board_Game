package com.example.boardgame.player;

public class Player {
    private String player_id;
    private String player_pw;
    private String player_name;
    private String player_nkname;
    private String player_bday;
    private int player_gender;
    private String player_star;
    private String player_email;
    private String player_area;
    private String fav_bg;
    private String adept_bg;
    private String player_intro;
    private int report_count;
    private int rate_count;
    private int rate_total;
    private int player_points;
    private String player_mood;

    // 建構式_所有屬性
    public Player(String player_id, String player_pw, String player_name, String player_nkname, String player_bday, int player_gender, String player_star, String player_email, String player_area, String fav_bg, String adept_bg, String player_intro, int report_count, int rate_count, int rate_total, int player_points, String player_mood) {
        this.player_id = player_id;
        this.player_pw = player_pw;
        this.player_name = player_name;
        this.player_nkname = player_nkname;
        this.player_bday = player_bday;
        this.player_gender = player_gender;
        this.player_star = player_star;
        this.player_email = player_email;
        this.player_area = player_area;
        this.fav_bg = fav_bg;
        this.adept_bg = adept_bg;
        this.player_intro = player_intro;
        this.report_count = report_count;
        this.rate_count = rate_count;
        this.rate_total = rate_total;
        this.player_points = player_points;
        this.player_mood = player_mood;
    }

    // 建構式_註冊
    public Player(String player_id, String player_pw, String player_name, String player_nkname, int player_gender, String player_bday) {
        this.player_id = player_id;
        this.player_pw = player_pw;
        this.player_name = player_name;
        this.player_nkname = player_nkname;
        this.player_gender = player_gender;
        this.player_bday = player_bday;
    }

    // 建構式＿個人資訊
    public Player(String player_id, int rate_count, int rate_total, String player_nkname, int player_gender, String player_star, String player_area, String fav_bg, String player_intro, String player_mood) {
        this.player_id = player_id;
        this.rate_count = rate_count;
        this.rate_total = rate_total;
        this.player_nkname = player_nkname;
        this.player_gender = player_gender;
        this.player_star = player_star;
        this.player_area = player_area;
        this.fav_bg = fav_bg;
        this.player_intro = player_intro;
        this.player_mood = player_mood;
    }


    public String getPlayer_id() {
        return player_id;
    }

    public void setPlayer_id(String player_id) {
        this.player_id = player_id;
    }

    public String getPlayer_pw() {
        return player_pw;
    }

    public void setPlayer_pw(String player_pw) {
        this.player_pw = player_pw;
    }

    public String getPlayer_name() {
        return player_name;
    }

    public void setPlayer_name(String player_name) {
        this.player_name = player_name;
    }

    public String getPlayer_nkname() {
        return player_nkname;
    }

    public void setPlayer_nkname(String player_nkname) {
        this.player_nkname = player_nkname;
    }

    public String getPlayer_bday() {
        return player_bday;
    }

    public void setPlayer_bday(String player_bday) {
        this.player_bday = player_bday;
    }

    public int getPlayer_gender() {
        return player_gender;
    }

    public void setPlayer_gender(int player_gender) {
        this.player_gender = player_gender;
    }

    public String getPlayer_star() {
        return player_star;
    }

    public void setPlayer_star(String player_star) {
        this.player_star = player_star;
    }

    public String getPlayer_email() {
        return player_email;
    }

    public void setPlayer_email(String player_email) {
        this.player_email = player_email;
    }

    public String getPlayer_area() {
        return player_area;
    }

    public void setPlayer_area(String player_area) {
        this.player_area = player_area;
    }

    public String getFav_bg() {
        return fav_bg;
    }

    public void setFav_bg(String fav_bg) {
        this.fav_bg = fav_bg;
    }

    public String getAdept_bg() {
        return adept_bg;
    }

    public void setAdept_bg(String adept_bg) {
        this.adept_bg = adept_bg;
    }

    public String getPlayer_intro() {
        return player_intro;
    }

    public void setPlayer_intro(String player_intro) {
        this.player_intro = player_intro;
    }

    public int getReport_count() {
        return report_count;
    }

    public void setReport_count(int report_count) {
        this.report_count = report_count;
    }

    public int getRate_count() {
        return rate_count;
    }

    public void setRate_count(int rate_count) {
        this.rate_count = rate_count;
    }

    public int getRate_total() {
        return rate_total;
    }

    public void setRate_total(int rate_total) {
        this.rate_total = rate_total;
    }

    public int getPlayer_points() {
        return player_points;
    }

    public void setPlayer_points(int player_points) {
        this.player_points = player_points;
    }

    public String getPlayer_mood() {
        return player_mood;
    }

    public void setPlayer_mood(String player_mood) {
        this.player_mood = player_mood;
    }
}
