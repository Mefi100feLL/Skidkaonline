package com.popcorp.parser.skidkaonline.entity;

public class Sale implements DomainObject{

    public static final String REPOSITORY = "saleRepository";

    private int id;
    private String shopUrl;
    private String imageSmall;
    private String imageBig;
    private long periodStart;
    private long periodEnd;
    private String catalog;
    private String cityUrl;
    private int cityId;
    private int imageWidth;
    private int imageHeight;
    private int countComments;

    public Sale(int id, String shopUrl, String imageSmall, String imageBig, long periodStart, long periodEnd, String catalog, String cityUrl, int cityId, int imageWidth, int imageHeight) {
        this.id = id;
        this.shopUrl = shopUrl;
        this.imageSmall = imageSmall;
        this.imageBig = imageBig;
        this.periodStart = periodStart;
        this.periodEnd = periodEnd;
        this.catalog = catalog;
        this.cityUrl = cityUrl;
        this.cityId = cityId;
        this.imageWidth = imageWidth;
        this.imageHeight = imageHeight;
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof Sale)) return false;
        Sale sale = (Sale) object;
        return getId() == sale.getId() && getCityUrl().equals(sale.getCityUrl());
    }

    public String getShopUrl() {
        return shopUrl;
    }

    public void setShopUrl(String shopUrl) {
        this.shopUrl = shopUrl;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getImageSmall() {
        return imageSmall;
    }

    public void setImageSmall(String imageSmall) {
        this.imageSmall = imageSmall;
    }

    public String getImageBig() {
        return imageBig;
    }

    public void setImageBig(String imageBig) {
        this.imageBig = imageBig;
    }

    public long getPeriodStart() {
        return periodStart;
    }

    public void setPeriodStart(long periodStart) {
        this.periodStart = periodStart;
    }

    public long getPeriodEnd() {
        return periodEnd;
    }

    public void setPeriodEnd(long periodEnd) {
        this.periodEnd = periodEnd;
    }

    public String getCatalog() {
        return catalog;
    }

    public void setCatalog(String catalog) {
        this.catalog = catalog;
    }

    public String getCityUrl() {
        return cityUrl;
    }

    public void setCityUrl(String cityUrl) {
        this.cityUrl = cityUrl;
    }

    public int getImageWidth() {
        return imageWidth;
    }

    public void setImageWidth(int imageWidth) {
        this.imageWidth = imageWidth;
    }

    public int getImageHeight() {
        return imageHeight;
    }

    public void setImageHeight(int imageHeight) {
        this.imageHeight = imageHeight;
    }

    public int getCityId() {
        return cityId;
    }

    public void setCityId(int cityId) {
        this.cityId = cityId;
    }

    public int getCountComments() {
        return countComments;
    }

    public void setCountComments(int countComments) {
        this.countComments = countComments;
    }
}
