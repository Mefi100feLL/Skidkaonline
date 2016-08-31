package com.popcorp.parser.skidkaonline.parser;

import com.popcorp.parser.skidkaonline.Application;
import com.popcorp.parser.skidkaonline.entity.Category;
import com.popcorp.parser.skidkaonline.entity.City;
import com.popcorp.parser.skidkaonline.entity.Shop;
import com.popcorp.parser.skidkaonline.loader.CategoriesLoader;
import com.popcorp.parser.skidkaonline.net.APIFactory;
import com.popcorp.parser.skidkaonline.repository.CategoryRepository;
import com.popcorp.parser.skidkaonline.util.ErrorManager;
import rx.Observable;

import java.io.IOException;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ShopsParser {

    public static Observable<ArrayList<Shop>> loadShops(City city) {
        CategoryRepository categoryRepository = Application.getCategoryRepository();
        return APIFactory.getAPI().getShops(city.getUrl())
                .flatMap(responseBody -> {
                    ArrayList<Shop> result = new ArrayList<>();
                    String page;
                    try {
                        page = responseBody.string();
                    } catch (IOException e) {
                        e.printStackTrace();
                        ErrorManager.sendError("SkidkaOnline: Page with shops not loaded! Error: " + e.getMessage());
                        return Observable.error(e);
                    }
                    Matcher shopMatcher = Pattern.compile(
                            "title=\"[.[^\"]]*\" href=\"" + city.getUrl() + "[.[^\"]]*\"> <span class=\"img\"><img width=\"[0-9]*\" height=\"[0-9]*\" src=\"[.[^\"]]*\" alt=\"[.[^\"]]*\" /></span> <span class=\"text\">[.[^<]]*</span>").matcher(page);
                    while (shopMatcher.find()) {
                        String shopResult = shopMatcher.group();
                        String name;
                        String url;
                        String image;
                        Category category = null;

                        Matcher categoryNameMatcher = Pattern.compile("title=\"[.[^\"]]*\"").matcher(shopResult);
                        if (categoryNameMatcher.find()) {
                            String categoryNameResult = categoryNameMatcher.group();
                            String categoryName = categoryNameResult.substring(7, categoryNameResult.length() - 1);
                            category = categoryRepository.getWithNameAndCityId(categoryName, city.getId());
                            if (category == null){
                                category = new CategoriesLoader().loadCategoriesForCity(city, categoryName);
                                if (category == null) {
                                    ErrorManager.sendError("SkidkaOnline: Category for shop not finded! Shop: " + shopResult);
                                    continue;
                                }
                            }
                        } else {
                            ErrorManager.sendError("SkidkaOnline: Category for shop not finded! Shop: " + shopResult);
                            continue;
                        }

                        Matcher nameMatcher = Pattern.compile("text\">[.[^<]]*</span>").matcher(shopResult);
                        if (nameMatcher.find()) {
                            String nameResult = nameMatcher.group();
                            name = nameResult.substring(6, nameResult.length() - 7);
                        } else {
                            ErrorManager.sendError("SkidkaOnline: Name for shop not finded! Shop: " + shopResult);
                            continue;
                        }

                        Matcher urlMatcher = Pattern.compile("href=\"" + city.getUrl() + "[.[^\"]]*\"").matcher(shopResult);
                        if (urlMatcher.find()) {
                            String urlResult = urlMatcher.group();
                            url = urlResult.substring(city.getUrl().length() + 6, urlResult.length() - 1);
                        } else {
                            ErrorManager.sendError("SkidkaOnline: Count for shop not finded! Shop: " + shopResult);
                            continue;
                        }

                        Matcher imageMatcher = Pattern.compile("src=\"[.[^\"]]*\"").matcher(shopResult);
                        if (imageMatcher.find()) {
                            String imageResult = imageMatcher.group();
                            image = imageResult.substring(5, imageResult.length() - 1).replaceFirst("-[0-9]*\\.", ".").replaceAll("\\?t=t[0-9]*", "");
                        } else {
                            ErrorManager.sendError("SkidkaOnline: Count for shop not finded! Shop: " + shopResult);
                            continue;
                        }
                        Shop shop = new Shop(name, image, url, category, category.getCityUrl(), category.getCityId());
                        if (!result.contains(shop)) {
                            result.add(shop);
                        }
                    }
                    return Observable.just(result);
                });
    }
}
