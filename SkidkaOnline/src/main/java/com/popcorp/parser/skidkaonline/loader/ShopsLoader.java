package com.popcorp.parser.skidkaonline.loader;

import com.popcorp.parser.skidkaonline.Application;
import com.popcorp.parser.skidkaonline.entity.City;
import com.popcorp.parser.skidkaonline.entity.Shop;
import com.popcorp.parser.skidkaonline.net.APIFactory;
import com.popcorp.parser.skidkaonline.parser.ShopsParser;
import com.popcorp.parser.skidkaonline.repository.ShopsRepository;
import com.popcorp.parser.skidkaonline.util.ErrorManager;
import rx.Observer;

import java.util.ArrayList;

public class ShopsLoader {

    private ShopsRepository shopsRepository = Application.getShopsRepository();

    public void loadShops() {
        try {
            Application.getCityRepository().getObservableAll()
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
                            loadShopsForCity(city);
                        }
                    });
        } catch (Exception e){
            ErrorManager.sendError("SkidkaOnline: Error loading shops error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void loadShopsForCity(City city) {
        ShopsParser.loadShops(city)
                .subscribeOn(APIFactory.getScheduler())
                .subscribe(new Observer<ArrayList<Shop>>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        ErrorManager.sendError("SkidkaOnline: Error loading shops error: " + e.getMessage());
                        e.printStackTrace();
                    }

                    @Override
                    public void onNext(ArrayList<Shop> shops) {
                        shopsRepository.save(shops);
                    }
                });
    }
}
