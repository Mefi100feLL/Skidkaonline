package com.popcorp.parser.skidkaonline.util;

import com.popcorp.parser.skidkaonline.loader.*;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

@EnableScheduling
public class ScheduledTasks {

    @Scheduled(fixedRate = 3600000, initialDelay = 7200000)
    public void clearOldSales() {
        new SalesCleaner().clearOldSales();
    }

    @Scheduled(fixedRate = 86400000, initialDelay = 1000)
    public void loadCities() {
        new CitiesLoader().loadCities();
    }

    @Scheduled(fixedRate = 86400000, initialDelay = 3600000)//10000
    public void loadShops() {
        new ShopsLoader().loadShops();
    }

    @Scheduled(fixedRate = 86400000, initialDelay = 1800000)//86400000
    public void loadCategories() {
        new CategoriesLoader().loadCategories();
    }

    @Scheduled(fixedRate = 21600000, initialDelay = 5400000)//3600000
    public void loadSales() {
        new SalesLoader().loadSales();
    }

    /*@Scheduled(fixedRate = 3600000, initialDelay = 7200000)
    public void sendErrors() {
        for (Error error : Application.getErrorRepository().getAll()){
            ErrorManager.sendError(error.getSubject(), error.getBody());
        }
    }*/
}
