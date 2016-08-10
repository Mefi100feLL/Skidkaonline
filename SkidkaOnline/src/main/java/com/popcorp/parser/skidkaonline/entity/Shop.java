package com.popcorp.parser.skidkaonline.entity;

public class Shop implements DomainObject {

    public static final String REPOSITORY = "shopRepository";
    public static final String CITY_REPOSITORY = "shopCityRepository";

    private String name;
    private String image;
    private String url;
    private Category category;
    private String cityUrl;
    private int cityId;

    public Shop(String name, String image, String url, Category category, String cityUrl, int cityId) {
        this.name = name;
        this.image = image;
        this.url = url;
        this.category = category;
        this.cityUrl = cityUrl;
        this.cityId = cityId;
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof Shop)) return false;
        Shop shop = (Shop) object;
        return getUrl().equals(shop.getUrl()) && getCityUrl().equals(shop.getCityUrl());
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public String getCityUrl() {
        return cityUrl;
    }

    public void setCityUrl(String cityUrl) {
        this.cityUrl = cityUrl;
    }

    public int getCityId() {
        return cityId;
    }

    public void setCityId(int cityId) {
        this.cityId = cityId;
    }
}
