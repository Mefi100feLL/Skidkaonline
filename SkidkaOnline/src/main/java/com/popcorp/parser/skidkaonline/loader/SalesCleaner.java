package com.popcorp.parser.skidkaonline.loader;

import com.popcorp.parser.skidkaonline.Application;
import com.popcorp.parser.skidkaonline.entity.City;
import com.popcorp.parser.skidkaonline.entity.Sale;
import com.popcorp.parser.skidkaonline.entity.Shop;
import com.popcorp.parser.skidkaonline.repository.CityRepository;
import com.popcorp.parser.skidkaonline.repository.SaleRepository;
import com.popcorp.parser.skidkaonline.repository.ShopsRepository;
import com.popcorp.parser.skidkaonline.util.ErrorManager;

import java.util.Calendar;

public class SalesCleaner {

    public void clearOldSales() {
        new Thread(() -> {
            try {
                ShopsRepository shopsRepository = Application.getShopsRepository();
                SaleRepository saleRepository = Application.getSaleRepository();
                CityRepository cityRepository = Application.getCityRepository();
                for (City city : cityRepository.getAll()) {
                    Calendar cityTime = Calendar.getInstance();
                    Iterable<Shop> shops = shopsRepository.getForCity(city.getId());
                    for (Shop shop : shops) {
                        for (Sale sale : saleRepository.getForShop(city.getId(), shop.getUrl())) {
                            Calendar saleTime = Calendar.getInstance();
                            saleTime.setTimeInMillis(sale.getPeriodEnd());
                            saleTime.add(Calendar.DAY_OF_YEAR, 1);
                            saleTime.set(Calendar.HOUR_OF_DAY, 0);
                            saleTime.set(Calendar.MINUTE, 30);
                            if (cityTime.getTimeInMillis() > saleTime.getTimeInMillis()) {
                                saleRepository.remove(sale);
                            }
                        }
                    }
                }
            } catch (Exception e) {
                ErrorManager.sendError("Mestoskidki: Error clearning sales error: " + e.getMessage());
                e.printStackTrace();
            }
        }).start();
    }
}
