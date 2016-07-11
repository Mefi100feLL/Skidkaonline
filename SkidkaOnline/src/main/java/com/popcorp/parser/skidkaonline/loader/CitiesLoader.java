package com.popcorp.parser.skidkaonline.loader;

import com.popcorp.parser.skidkaonline.Application;
import com.popcorp.parser.skidkaonline.entity.City;
import com.popcorp.parser.skidkaonline.net.APIFactory;
import com.popcorp.parser.skidkaonline.parser.CitiesParser;
import com.popcorp.parser.skidkaonline.repository.CityRepository;
import com.popcorp.parser.skidkaonline.util.ErrorManager;
import rx.Observer;

public class CitiesLoader {

    private CityRepository cityRepository;

    public void loadCities() {
        try {
            cityRepository = Application.getCityRepository();
            CitiesParser.loadCities()
                    .subscribeOn(APIFactory.getScheduler())
                    .subscribe(new Observer<City>() {
                        @Override
                        public void onCompleted() {

                        }

                        @Override
                        public void onError(Throwable e) {
                            ErrorManager.sendError("SkidkaOnline: Error loading citites error: " + e.getMessage());
                            e.printStackTrace();
                        }

                        @Override
                        public void onNext(City city) {
                            cityRepository.save(city);
                        }
                    });
        } catch (Exception e) {
            ErrorManager.sendError("SkidkaOnline: Error loading citites error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
