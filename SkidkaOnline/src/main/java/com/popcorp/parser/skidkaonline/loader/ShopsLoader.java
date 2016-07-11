package com.popcorp.parser.skidkaonline.loader;

import com.popcorp.parser.skidkaonline.Application;
import com.popcorp.parser.skidkaonline.entity.Category;
import com.popcorp.parser.skidkaonline.entity.City;
import com.popcorp.parser.skidkaonline.entity.Shop;
import com.popcorp.parser.skidkaonline.net.APIFactory;
import com.popcorp.parser.skidkaonline.parser.ShopsParser;
import com.popcorp.parser.skidkaonline.repository.CategoryRepository;
import com.popcorp.parser.skidkaonline.repository.CityRepository;
import com.popcorp.parser.skidkaonline.repository.ShopsRepository;
import com.popcorp.parser.skidkaonline.util.ErrorManager;
import rx.Observer;

import java.util.ArrayList;
import java.util.List;

public class ShopsLoader {

    private ShopsRepository shopsRepository;

    public void loadShops() {
        try {
            shopsRepository = Application.getShopsRepository();
            CityRepository cityRepository = Application.getCityRepository();
            for (City city : cityRepository.getAll()){
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
        } catch (Exception e){
            ErrorManager.sendError("SkidkaOnline: Error loading shops error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
