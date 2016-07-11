package com.popcorp.parser.skidkaonline.entity;

public class City implements DomainObject {

    public static final String REPOSITORY = "cityRepository";

    private int id;
    private String name;
    private String url;
    private String region;

    public City(int id, String name, String url, String region) {
        this.id = id;
        this.name = name;
        this.url = url;
        this.region = region;
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof City)) return false;
        City city = (City) object;
        return getUrl().equals(city.getUrl());
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }
}
