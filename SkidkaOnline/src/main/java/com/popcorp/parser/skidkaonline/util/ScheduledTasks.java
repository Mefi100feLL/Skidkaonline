package com.popcorp.parser.skidkaonline.util;

import com.popcorp.parser.skidkaonline.loader.*;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

@EnableScheduling
public class ScheduledTasks {

    private static final long SECOND = 1000;

    private static final long MINUTE = 60 * SECOND;

    private static final long HALF_HOUR = 30 * MINUTE;
    private static final long HOUR = 60 * MINUTE;

    private static final long DAY = 24 * HOUR;


    @Scheduled(fixedRate = DAY, initialDelay = HALF_HOUR)
    public void loadCities() {
        new CitiesLoader().loadCities();
    }

    @Scheduled(fixedRate = DAY, initialDelay = 2 * HALF_HOUR)//86400000
    public void loadCategories() {
        new CategoriesLoader().loadCategories();
    }

    @Scheduled(fixedRate = DAY, initialDelay = 3 * HALF_HOUR)//3600000
    public void loadShops() {
        new ShopsLoader().loadShops();
    }

    @Scheduled(fixedRate = 4 * HOUR, initialDelay = 4 * HALF_HOUR)//5400000
    public void loadSales() {
        new SalesLoader().loadSales();
    }

    @Scheduled(fixedRate = HOUR, initialDelay = 5 * HALF_HOUR)
    public void clearOldSales() {
        new SalesCleaner().clearOldSales();
    }

/*
    @Scheduled(fixedRate = 21600000, initialDelay = 1000)//5400000
    public void test() {
        Iterable<Shop> shops = Application.getShopsRepository().getForCity(14310);
        for (Shop shop : shops) {
            SalesParser.loadSales(shop)
                    .subscribe(new Observer<ArrayList<Sale>>() {
                        @Override
                        public void onCompleted() {

                        }

                        @Override
                        public void onError(Throwable e) {
                            int a = 0;
                        }

                        @Override
                        public void onNext(ArrayList<Sale> sales) {
                            int a= 0;
                        }
                    });
        }
    }*/

    /*@Scheduled(fixedRate = 3600000, initialDelay = 7200000)
    public void sendErrors() {
        for (Error error : Application.getErrorRepository().getAll()){
            ErrorManager.sendError(error.getSubject(), error.getBody());
        }
    }*/
}
