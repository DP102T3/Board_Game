package com.example.boardgame.advertisement_points;

public class AdNow {
    private String duration;
    private String status;
    private String adPic;

    public AdNow(String duration, String status, String adPic) {
        this.duration = duration;
        this.status = status;
        this.adPic = adPic;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getAdPic() {
        return adPic;
    }

    public void setAdPic(String adPic) {
        this.adPic = adPic;
    }
}
