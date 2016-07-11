package com.popcorp.parser.skidkaonline;

import com.popcorp.parser.skidkaonline.config.JpaConfig;
import com.popcorp.parser.skidkaonline.repository.*;
import com.popcorp.parser.skidkaonline.util.ScheduledTasks;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.web.SpringBootServletInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan
@EnableAutoConfiguration
public class Application extends SpringBootServletInitializer {

    private static CityRepository cityRep;
    private static SaleRepository saleRep;
    private static ShopsRepository shopsRep;
    private static CategoryRepository categoryRep;
    private static ErrorRepository errorRep;

    public static void main(String[] args) {
        SpringApplication.run(new Class<?>[]{Application.class, JpaConfig.class, ScheduledTasks.class}, args);
    }

    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(Application.class, JpaConfig.class, ScheduledTasks.class).web(false);
    }


    public static ErrorRepository getErrorRepository() {
        return errorRep;
    }

    public static ShopsRepository getShopsRepository(){
        return shopsRep;
    }

    public static CityRepository getCityRepository(){
        return cityRep;
    }

    public static CategoryRepository getCategoryRepository(){
        return categoryRep;
    }

    public static SaleRepository getSaleRepository(){
        return saleRep;
    }


    @Bean
    public CommandLineRunner clearOldSales(CityRepository cityRepository,
                                           SaleRepository saleRepository,
                                           ShopsRepository shopsRepository,
                                           CategoryRepository categoryRepository,
                                           ErrorRepository errorRepository) {
        cityRep = cityRepository;
        saleRep = saleRepository;
        shopsRep = shopsRepository;
        categoryRep = categoryRepository;
        errorRep = errorRepository;
        return args -> {};
    }

    /*@Bean
    public CommandLineRunner clearOldSales(CityRepository cityRepository, SaleRepository saleRepository) {
        return args -> {};//new SalesCleaner().clearOldSales(cityRepository, saleRepository);
    }

    @Bean
    public CommandLineRunner loadCities(CityRepository cityRepository){
        return args -> new CitiesLoader().loadCities(cityRepository);
    }

    @Bean
    public CommandLineRunner loadShops(CityRepository cityRepository, ShopsRepository shopsRepository){
        return args -> new ShopsLoader().loadShops(cityRepository, shopsRepository);
    }

    @Bean
    public CommandLineRunner loadCategories(CategoryRepository categoryRepository){
        return args -> new CategoriesLoader().loadCategories(categoryRepository);
    }

    @Bean
    public CommandLineRunner loadCategoriesInners(CategoryInnerRepository categoryInnerRepository){
        return args -> new CategoriesInnerLoader().loadCategories(categoryInnerRepository);
    }

    @Bean
    public CommandLineRunner loadSales(ShopsRepository shopsRepository, SaleRepository saleRepository, CategoryInnerRepository categoryInnerRepository){
        return args -> new SalesLoader().loadSales(shopsRepository, saleRepository, categoryInnerRepository);
    }*/
}
