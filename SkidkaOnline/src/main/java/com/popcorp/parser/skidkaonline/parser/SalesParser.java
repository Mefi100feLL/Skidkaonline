package com.popcorp.parser.skidkaonline.parser;

import com.popcorp.parser.skidkaonline.entity.Sale;
import com.popcorp.parser.skidkaonline.entity.Shop;
import com.popcorp.parser.skidkaonline.error.ShopNoFoundException;
import com.popcorp.parser.skidkaonline.net.APIFactory;
import com.popcorp.parser.skidkaonline.util.ErrorManager;
import rx.Observable;
import rx.Subscriber;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SalesParser {

    public static Observable<ArrayList<Sale>> loadSales(Shop shop) {
        return Observable.create(new Observable.OnSubscribe<ArrayList<Sale>>() {
            @Override
            public void call(Subscriber<? super ArrayList<Sale>> subscriber) {
                int page = 1;
                while (true){
                    ArrayList<Sale> sales = getSales(shop, page++);
                    if (sales != null){
                        subscriber.onNext(sales);
                    } else{
                        subscriber.onCompleted();
                        break;
                    }
                }
            }
        });
    }

    private static ArrayList<Sale> getSales(Shop shop, int page) {
        return APIFactory.getAPI().getSales(shop.getCityUrl(), shop.getUrl(), 1, page)
                .map(responseBody -> {
                    String pageString;
                    try {
                        pageString = responseBody.string();
                    } catch (IOException e) {
                        e.printStackTrace();
                        ErrorManager.sendError("SkidkaOnline: Page with sales not loaded! Shop: " + shop.getUrl() + ", city: " + shop.getCityUrl() + " page: " + page + ", error: " + e.getMessage());
                        return null;
                    }
                    return getSalesForPage(shop, pageString);
                })
                .onErrorResumeNext(throwable -> {
                    return Observable.error(new ShopNoFoundException(shop, throwable));
                })
                .toBlocking()
                .first();
    }

    private static ArrayList<Sale> getSalesForPage(Shop shop, String page) {
        SimpleDateFormat periodStartFormat = new SimpleDateFormat("d MMMM", new Locale("ru"));
        SimpleDateFormat periodEndFormat = new SimpleDateFormat("d MMMM yyyy", new Locale("ru"));

        ArrayList<Sale> sales = new ArrayList<>();

        ArrayList<Integer> ids = new ArrayList<>();

        ArrayList<String> catalogs = new ArrayList<>();
        ArrayList<Long> periodsStart = new ArrayList<>();
        ArrayList<Long> periodsEnd = new ArrayList<>();

        ArrayList<String> smallImages = new ArrayList<>();
        ArrayList<String> bigImages = new ArrayList<>();
        ArrayList<Integer> widths = new ArrayList<>();
        ArrayList<Integer> heights = new ArrayList<>();

        Matcher idMatcher = Pattern.compile("data-id=\"[0-9]*\"").matcher(page);
        while (idMatcher.find()) {
            String idResult = idMatcher.group();
            ids.add(Integer.valueOf(idResult.substring(9, idResult.length() - 1)));
        }

        Matcher catalogMatcher = Pattern.compile("<a class=\"discount-link\" href=\"[.[^\"]]*\"><strong>[.[^<]]*</strong><br/>\\([.[^\\)]]*\\)</a>").matcher(page);
        while (catalogMatcher.find()) {
            String catalogResult = catalogMatcher.group();
            Matcher catalogNameMatcher = Pattern.compile("<strong>[.[^<]]*<").matcher(catalogResult);
            if (catalogNameMatcher.find()) {
                String catalogNameResult = catalogNameMatcher.group();
                catalogs.add(catalogNameResult.substring(8, catalogNameResult.length() - 1));
            }
            Matcher catalogPeriodsMatcher = Pattern.compile("<br/>\\([.[^\\)]]*\\)").matcher(catalogResult);
            if (catalogPeriodsMatcher.find()) {
                String catalogPeriodsResult = catalogPeriodsMatcher.group().replaceAll("<br/>|\\(|\\)", "").toLowerCase();
                String[] split = catalogPeriodsResult.split(" - ");
                int year = 0;
                if (split.length > 1){
                    try {
                        Calendar periodEnd = Calendar.getInstance();
                        periodEnd.setTime(periodEndFormat.parse(split[1]));
                        year = periodEnd.get(Calendar.YEAR);
                        periodsEnd.add(periodEnd.getTimeInMillis());
                    } catch (Exception e) {
                        ErrorManager.sendError("SkidkaOnline: PeriodEnd for sale not parsed! catalogPeriodsResult: " + catalogPeriodsResult + ", error: " + e.getMessage());
                        e.printStackTrace();
                    }
                }
                try {
                    Calendar periodStart = Calendar.getInstance();
                    if (year == 0) {
                        periodStart.setTime(periodEndFormat.parse(split[0]));
                    } else{
                        periodStart.setTime(periodStartFormat.parse(split[0]));
                        periodStart.set(Calendar.YEAR, year);
                    }
                    if (split.length == 1){
                        periodsEnd.add(periodStart.getTimeInMillis());
                    }
                    periodsStart.add(periodStart.getTimeInMillis());
                } catch (Exception e) {
                    try {
                        Calendar periodStart = Calendar.getInstance();
                        periodStart.setTime(periodEndFormat.parse(split[0]));
                        periodsStart.add(periodStart.getTimeInMillis());
                    } catch (Exception e1) {
                        ErrorManager.sendError("SkidkaOnline: PeriodStart for sale not parsed! catalogPeriodsResult: " + catalogPeriodsResult + ", error: " + e.getMessage());
                        e1.printStackTrace();
                    }
                }
            }
        }

        Matcher imageMathcer = Pattern.compile("<img src=\"[.[^\"]]*\" data-fw=\"[0-9]*\" data-fh=\"[0-9]*\" height=\"[0-9]*\"").matcher(page);
        while (imageMathcer.find()) {
            String imageResult = imageMathcer.group();
            Matcher imageSmallMathcer = Pattern.compile("src=\"[.[^\"]]*\"").matcher(imageResult);
            if (imageSmallMathcer.find()) {
                String imageSmallResult = imageSmallMathcer.group();
                String smallImage = imageSmallResult.substring(5, imageSmallResult.length() - 1).replaceAll("\\?t=t[0-9]*", "");
                smallImages.add(smallImage);
                String bigImage = smallImage.replaceFirst("-[0-9]*\\.", ".");
                bigImages.add(bigImage);
            }
            Matcher heightMatcher = Pattern.compile("height=\"[0-9]*\"").matcher(imageResult);
            if (heightMatcher.find()) {
                String heightResult = heightMatcher.group();
                heights.add(Integer.valueOf(heightResult.substring(8, heightResult.length() - 1)));
            }

            widths.add(336);
            /*Matcher widthMatcher = Pattern.compile("width=\"[0-9]*\"").matcher(imageResult);
            if (widthMatcher.find()) {
                String widthResult = widthMatcher.group();
                widths.add(Integer.valueOf(widthResult.substring(7, widthResult.length() - 1)));
            }*/
        }

        if (ids.size() == catalogs.size() &&
                ids.size() == periodsEnd.size() &&
                ids.size() == periodsStart.size() &&
                ids.size() == bigImages.size() &&
                ids.size() == smallImages.size() &&
                ids.size() == widths.size() &&
                ids.size() == heights.size()) {
            for (int i = 0; i < ids.size(); i++) {
                Sale sale = new Sale(ids.get(i), shop.getUrl(), smallImages.get(i), bigImages.get(i), periodsStart.get(i), periodsEnd.get(i), catalogs.get(i), shop.getCityUrl(), shop.getCityId(), widths.get(i), heights.get(i));
                sales.add(sale);
            }
        } else {
            ErrorManager.sendError("SkidkaOnline: Fields for sale are not equals! cityUrl: " + shop.getCityUrl() + ", shopUrl: " + shop.getUrl() + ";");
        }

        if (sales.size() > 0) {
            return sales;
        } else {
            return null;
        }
    }
}
