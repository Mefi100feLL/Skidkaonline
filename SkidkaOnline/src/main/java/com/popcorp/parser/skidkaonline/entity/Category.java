package com.popcorp.parser.skidkaonline.entity;

public class Category implements DomainObject{

    public static final String REPOSITORY = "categoryRepository";

    private String name;
    private String url;
    private String cityUrl;
    private int cityId;

    public Category(String name, String url, String cityUrl, int cityId) {
        this.name = name;
        this.url = url;
        this.cityUrl = cityUrl;
        this.cityId = cityId;
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof Category)) return false;
        Category category = (Category) object;
        return getUrl().equals(category.getUrl()) && getCityUrl().equals(category.getCityUrl());
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
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
