package com.popcorp.parser.skidkaonline.parser;

import com.popcorp.parser.skidkaonline.entity.Category;
import com.popcorp.parser.skidkaonline.entity.City;
import com.popcorp.parser.skidkaonline.net.APIFactory;
import com.popcorp.parser.skidkaonline.util.ErrorManager;
import rx.Observable;

import java.io.IOException;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CategoriesParser {

    public static Observable<ArrayList<Category>> loadCategories(City city) {
        return APIFactory.getAPI().getCity(city.getUrl())
                .flatMap(responseBody -> {
                    ArrayList<Category> result = new ArrayList<>();
                    String page;
                    try {
                        page = responseBody.string();
                    } catch (IOException e) {
                        e.printStackTrace();
                        ErrorManager.sendError("SkidkaOnline: Page with categories not loaded! Error: " + e.getMessage());
                        return Observable.error(e);
                    }
                    Matcher categoryMatcher = Pattern.compile("<h3><a href=\"" + city.getUrl() + "[.[^\"]]*\">[.[^<]]*</a>").matcher(page);
                    while (categoryMatcher.find()) {
                        String categoryResult = categoryMatcher.group();
                        String name;
                        String url;
                        Matcher nameMatcher = Pattern.compile(">[.[^<]]*</a>").matcher(categoryResult);
                        if (nameMatcher.find()) {
                            String nameResult = nameMatcher.group();
                            name = nameResult.substring(1, nameResult.length() - 4);
                        } else {
                            ErrorManager.sendError("SkidkaOnline: Name for category not finded! Category: " + categoryResult);
                            continue;
                        }
                        Matcher urlMatcher = Pattern.compile(city.getUrl() + "[.[^\"]]*\">").matcher(categoryResult);
                        if (urlMatcher.find()) {
                            String urlResult = urlMatcher.group();
                            url = urlResult.substring(city.getUrl().length(), urlResult.length() - 2);
                        } else {
                            ErrorManager.sendError("SkidkaOnline: Url for category not finded! Category: " + categoryResult);
                            continue;
                        }
                        Category category = new Category(name, url, city.getUrl(), city.getId());
                        if (!result.contains(category)) {
                            result.add(category);
                        }
                    }
                    return Observable.just(result);
                });
    }
}
