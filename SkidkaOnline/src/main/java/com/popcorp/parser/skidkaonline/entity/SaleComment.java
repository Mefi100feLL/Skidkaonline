package com.popcorp.parser.skidkaonline.entity;

import com.google.gson.annotations.SerializedName;

public class SaleComment {

    private int saleId;

    @SerializedName("username")
    private String userName;

    @SerializedName("created")
    private String createdTime;

    @SerializedName("comment")
    private String text;

    @SerializedName("cityname")
    private String cityName;

    public SaleComment(int saleId, String userName, String createdTime, String text, String cityName) {
        this.saleId = saleId;
        this.userName = userName;
        this.createdTime = createdTime;
        this.text = text;
        this.cityName = cityName;
    }

    public int getSaleId() {
        return saleId;
    }

    public void setSaleId(int saleId) {
        this.saleId = saleId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(String createdTime) {
        this.createdTime = createdTime;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }
}
