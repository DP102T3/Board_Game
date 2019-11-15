package com.example.boardgame.advertisement_points;

import java.util.List;

public class AdvertisementListResult {
    private List<Advertisement> result;

    public List<Advertisement> getResult() {
        return result;
    }

    public void setResult(List<Advertisement> result) {
        this.result = result;
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();

        int i = 1;

        for (Advertisement advertisement: result) {
            stringBuilder.append("Object" + i + " - ").append(advertisement.toString()).append("\n");
            i++;
        }

        return stringBuilder.toString();
    }
}
