package com.popcorp.parser.skidkaonline.parser;

import com.popcorp.parser.skidkaonline.entity.City;
import com.popcorp.parser.skidkaonline.net.APIFactory;
import com.popcorp.parser.skidkaonline.util.ErrorManager;
import okhttp3.ResponseBody;
import rx.Observable;
import rx.functions.Func1;

import java.io.IOException;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CitiesParser {

    public static Observable<City> loadCities() {
        return getPages()
                .flatMap(strings -> {
                    ArrayList<Observable<City>> result = new ArrayList<>();
                    for (String page : strings) {
                        result.add(getCities(page));
                    }
                    return Observable.merge(result);
                });
    }

    private static Observable<ArrayList<String>> getPages() {
        return APIFactory.getAPI().getAllCities(1)
                .flatMap(responseBody -> {
                    ArrayList<String> pages = new ArrayList<>();
                    String page;
                    try {
                        page = responseBody.string();
                    } catch (IOException e) {
                        e.printStackTrace();
                        ErrorManager.sendError("SkidkaOnline: Page with cities not loaded! Error: " + e.getMessage());
                        return Observable.error(e);
                    }
                    Matcher pageMatcher = Pattern.compile("<a href=\"/cities/\\?a=[.[^\"]]*\"").matcher(page);
                    while (pageMatcher.find()) {
                        String pageResult = pageMatcher.group();
                        String[] split = pageResult.split("a=");
                        if (split.length == 2) {
                            pages.add(split[1].replaceAll("\"", ""));
                        }
                    }
                    return Observable.just(pages);
                });
    }

    private static Observable<City> getCities(String pageUrl) {
        return APIFactory.getAPI().getCitiesForPage(pageUrl)
                .flatMap(new Func1<ResponseBody, Observable<ArrayList<City>>>() {
                    @Override
                    public Observable<ArrayList<City>> call(ResponseBody responseBody) {
                        String page;
                        try {
                            page = responseBody.string();
                        } catch (IOException e) {
                            e.printStackTrace();
                            ErrorManager.sendError("SkidkaOnline: Page with cities not loaded! Page=" + pageUrl + ", Error: " + e.getMessage());
                            return Observable.error(e);
                        }
                        return Observable.just(getCitiesForPage(page));
                    }
                })
                .flatMap(new Func1<ArrayList<City>, Observable<City>>() {
                    @Override
                    public Observable<City> call(ArrayList<City> cities) {
                        ArrayList<Observable<City>> result = new ArrayList<>();
                        for (City city : cities) {
                            result.add(getCityWithId(city));
                        }
                        return Observable.merge(result);
                    }
                });
    }

    private static Observable<City> getCityWithId(City city) {
        return APIFactory.getAPI().getCity(city.getUrl())
                .flatMap(new Func1<ResponseBody, Observable<City>>() {
                    @Override
                    public Observable<City> call(ResponseBody responseBody) {
                        String page;
                        try {
                            page = responseBody.string();
                        } catch (IOException e) {
                            e.printStackTrace();
                            ErrorManager.sendError("SkidkaOnline: Page for city not loaded! Error: " + e.getMessage());
                            return Observable.error(e);
                        }
                        int id = findCityId(page);
                        if (id > 0) {
                            city.setId(id);
                        } else {
                            ErrorManager.sendError("SkidkaOnline: Id for city not finded! City: " + city.getUrl());
                            return Observable.empty();
                        }
                        return Observable.just(city);
                    }
                });
    }

    private static int findCityId(String page) {
        int result = -1;
        Matcher idMatcher = Pattern.compile("<input data-ajax-cityid=\"[0-9]*\"").matcher(page);
        if (idMatcher.find()) {
            String idResult = idMatcher.group();
            String[] split = idResult.split("=");
            if (split.length == 2) {
                result = Integer.valueOf(split[1].replaceAll("\"", ""));
            }
        }
        return result;
    }


    private static ArrayList<City> getCitiesForPage(String page) {
        ArrayList<City> result = new ArrayList<>();
        Matcher cityMatcher = Pattern.compile("<a data-toggle=\"tooltip\" title=\"[.[^\"]]*\" href=\"[.[^\"]]*\"><span class=\"text\">[.[^<]]*</span></a>").matcher(page);
        while (cityMatcher.find()) {
            String cityResult = cityMatcher.group();

            String region = "";
            String url;
            String name;

            Matcher regionMatcher = Pattern.compile("title=\"[.[^\"]]*\"").matcher(cityResult);
            if (regionMatcher.find()) {
                String regionResult = regionMatcher.group();
                region = regionResult.substring(7, regionResult.length() - 1);
            }
            Matcher urlMatcher = Pattern.compile("href=\"[.[^\"]]*\"").matcher(cityResult);
            if (urlMatcher.find()) {
                String urlResult = urlMatcher.group();
                url = urlResult.substring(6, urlResult.length() - 1);
            } else {
                ErrorManager.sendError("SkidkaOnline: URL for city not finded! City=" + cityResult);
                continue;
            }
            Matcher nameMacther = Pattern.compile("text\">[.[^<]]*<").matcher(cityResult);
            if (nameMacther.find()) {
                String nameResult = nameMacther.group();
                name = nameResult.substring(6, nameResult.length() - 1);
            } else {
                ErrorManager.sendError("SkidkaOnline: Name for city not finded! City=" + cityResult);
                continue;
            }
            City city = new City(0, name, url, region);
            if (!result.contains(city)) {
                result.add(city);
            }
        }
        return result;
    }
}
