package com.popcorp.parser.skidkaonline.loader;

import com.popcorp.parser.skidkaonline.Application;
import com.popcorp.parser.skidkaonline.entity.City;
import com.popcorp.parser.skidkaonline.entity.Sale;
import com.popcorp.parser.skidkaonline.entity.Shop;
import com.popcorp.parser.skidkaonline.error.ShopNoFoundException;
import com.popcorp.parser.skidkaonline.net.APIFactory;
import com.popcorp.parser.skidkaonline.parser.SalesParser;
import com.popcorp.parser.skidkaonline.repository.CityRepository;
import com.popcorp.parser.skidkaonline.repository.SaleRepository;
import com.popcorp.parser.skidkaonline.repository.ShopsRepository;
import com.popcorp.parser.skidkaonline.util.ErrorManager;
import rx.Observer;

import java.util.ArrayList;

public class SalesLoader {

    private SaleRepository saleRepository = Application.getSaleRepository();
    private ShopsRepository shopsRepository = Application.getShopsRepository();

    public void loadSales() {
        try {
            CityRepository cityRepository = Application.getCityRepository();
            cityRepository.getObservableAll()
                    .subscribeOn(APIFactory.getScheduler())
                    .subscribe(new Observer<City>() {
                        @Override
                        public void onCompleted() {

                        }

                        @Override
                        public void onError(Throwable e) {
                            ErrorManager.sendError("SkidkaOnline: Error loading cities from DB error: " + e.getMessage());
                            e.printStackTrace();
                        }

                        @Override
                        public void onNext(City city) {
                            loadSalesForCity(city);
                        }
                    });
        } catch (Exception e) {
            ErrorManager.sendError("SkidkaOnline: Error loading sales error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void loadSalesForCity(City city) {
        shopsRepository.getObservableForCity(city.getId())
                .subscribeOn(APIFactory.getScheduler())
                .subscribe(new Observer<Shop>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        ErrorManager.sendError("SkidkaOnline: Error loading shops for city from DB error: " + e.getMessage());
                        e.printStackTrace();
                    }

                    @Override
                    public void onNext(Shop shop) {
                        loadSalesForShop(shop);
                    }
                });
    }

    private void loadSalesForShop(Shop shop) {
        SalesParser.loadSales(shop)
                .subscribeOn(APIFactory.getScheduler())
                .subscribe(new Observer<ArrayList<Sale>>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        if (e instanceof ShopNoFoundException) {
                            shopsRepository.remove(((ShopNoFoundException) e).getShop());
                        } else if (e.getCause() instanceof ShopNoFoundException) {
                            shopsRepository.remove(((ShopNoFoundException) e.getCause()).getShop());
                        } else {
                            ErrorManager.sendError("SkidkaOnline: Error loading sales error: " + e.getMessage());
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onNext(ArrayList<Sale> sale) {
                        saleRepository.save(sale);
                    }
                });
    }
}
