package com.popcorp.parser.skidkaonline.loader;

import com.popcorp.parser.skidkaonline.Application;
import com.popcorp.parser.skidkaonline.entity.City;
import com.popcorp.parser.skidkaonline.entity.Sale;
import com.popcorp.parser.skidkaonline.entity.Shop;
import com.popcorp.parser.skidkaonline.net.APIFactory;
import com.popcorp.parser.skidkaonline.parser.SalesParser;
import com.popcorp.parser.skidkaonline.repository.CityRepository;
import com.popcorp.parser.skidkaonline.repository.SaleRepository;
import com.popcorp.parser.skidkaonline.repository.ShopsRepository;
import com.popcorp.parser.skidkaonline.util.ErrorManager;
import rx.Observer;

import java.util.ArrayList;

public class SalesLoader {

    private SaleRepository saleRepository;

    public void loadSales() {
        try {
            ShopsRepository shopsRepository = Application.getShopsRepository();
            saleRepository = Application.getSaleRepository();
            CityRepository cityRepository = Application.getCityRepository();
            for (City city : cityRepository.getAll()){
                Iterable<Shop> shops = shopsRepository.getForCity(city.getId());
                for (Shop shop : shops) {
                    SalesParser.loadSales(shop)
                            .subscribeOn(APIFactory.getScheduler())
                            .subscribe(new Observer<ArrayList<Sale>>() {
                                @Override
                                public void onCompleted() {

                                }

                                @Override
                                public void onError(Throwable e) {
                                    ErrorManager.sendError("SkidkaOnline: Error loading sales error: " + e.getMessage());
                                    e.printStackTrace();
                                }

                                @Override
                                public void onNext(ArrayList<Sale> sale) {
                                    saleRepository.save(sale);
                                }
                            });

                }
            }
        } catch (Exception e) {
            ErrorManager.sendError("SkidkaOnline: Error loading sales error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
