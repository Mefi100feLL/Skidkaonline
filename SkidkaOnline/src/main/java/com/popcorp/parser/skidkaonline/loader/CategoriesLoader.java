package com.popcorp.parser.skidkaonline.loader;

import com.popcorp.parser.skidkaonline.Application;
import com.popcorp.parser.skidkaonline.entity.Category;
import com.popcorp.parser.skidkaonline.entity.City;
import com.popcorp.parser.skidkaonline.net.APIFactory;
import com.popcorp.parser.skidkaonline.parser.CategoriesParser;
import com.popcorp.parser.skidkaonline.repository.CategoryRepository;
import com.popcorp.parser.skidkaonline.repository.CityRepository;
import com.popcorp.parser.skidkaonline.util.ErrorManager;
import rx.Observer;

import java.util.ArrayList;

public class CategoriesLoader {

    private CategoryRepository categoryRepository;

    public void loadCategories() {
        try {
            categoryRepository = Application.getCategoryRepository();
            CityRepository cityRepository = Application.getCityRepository();
            for (City city : cityRepository.getAll()){
                CategoriesParser.loadCategories(city)
                        .subscribeOn(APIFactory.getScheduler())
                        .subscribe(new Observer<ArrayList<Category>>() {
                            @Override
                            public void onCompleted() {

                            }

                            @Override
                            public void onError(Throwable e) {
                                ErrorManager.sendError("Mestoskidki: Error loading categories error: " + e.getMessage());
                                e.printStackTrace();
                            }

                            @Override
                            public void onNext(ArrayList<Category> categories) {
                                categoryRepository.save(categories);
                            }
                        });
            }
        } catch (Exception e){
            ErrorManager.sendError("Mestoskidki: Error loading categories error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
