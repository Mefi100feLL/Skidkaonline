package com.popcorp.parser.skidkaonline.controller;

import com.popcorp.parser.skidkaonline.dto.UniversalDTO;
import com.popcorp.parser.skidkaonline.entity.*;
import com.popcorp.parser.skidkaonline.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Calendar;

@org.springframework.web.bind.annotation.RestController
public class RestController {

    @Autowired
    @Qualifier(City.REPOSITORY)
    private CityRepository cityRepository;

    @Autowired
    @Qualifier(Category.REPOSITORY)
    private CategoryRepository categoryRepository;

    @Autowired
    @Qualifier(Shop.REPOSITORY)
    private ShopsRepository shopRepository;

    @Autowired
    @Qualifier(Sale.REPOSITORY)
    private SaleRepository saleRepository;

    @Autowired
    @Qualifier(SaleComment.REPOSITORY)
    private SaleCommentRepository saleCommentRepository;


    @RequestMapping("/cities")
    public UniversalDTO<Iterable<City>> getCities() {
        UniversalDTO<Iterable<City>> result = new UniversalDTO<>(true, "Ошибка при поиске городов", null);
        Iterable<City> cities = cityRepository.getAll();
        if (cities != null) {
            result = new UniversalDTO<>(false, "", cities);
        }
        return result;
    }

    @RequestMapping("/categories")
    public UniversalDTO<Iterable<Category>> getCategories(@RequestParam(value = "city", defaultValue = "-1") int cityId) {
        if (cityId == -1) {
            return new UniversalDTO<>(true, "Не указан город", null);
        }
        UniversalDTO<Iterable<Category>> result = new UniversalDTO<>(true, "Ошибка при поиске категорий", null);
        Iterable<Category> categories = categoryRepository.getForCity(cityId);
        if (categories != null) {
            result = new UniversalDTO<>(false, "", categories);
        }
        return result;
    }

    @RequestMapping("/shops")
    public UniversalDTO<Iterable<Shop>> getShopsForCity(@RequestParam(value = "city", defaultValue = "-1") int cityId) {
        if (cityId == -1) {
            return new UniversalDTO<>(true, "Не указан город", null);
        }
        UniversalDTO<Iterable<Shop>> result = new UniversalDTO<>(true, "Ошибка при поиске магазинов", null);
        Iterable<Shop> shops = shopRepository.getForCity(cityId);
        if (shops != null) {
            result = new UniversalDTO<>(false, "", shops);
        }
        return result;
    }

    @RequestMapping("/sales")
    public UniversalDTO<Iterable<Sale>> getSales(
            @RequestParam(value = "city", defaultValue = "-1") int cityId,
            @RequestParam(value = "shop", defaultValue = "") String shop) {
        if (cityId == -1 || shop.isEmpty()) {
            return new UniversalDTO<>(true, "Неверные входные параметры", null);
        }
        UniversalDTO<Iterable<Sale>> result = new UniversalDTO<>(true, "Ошибка при поиске акций", null);
        Iterable<Sale> sales = saleRepository.getForShop(cityId, shop);
        if (sales != null) {
            result = new UniversalDTO<>(false, "", sales);
        }
        return result;
    }

    @RequestMapping("/sale")
    public UniversalDTO<Sale> getSale(
            @RequestParam(value = "city", defaultValue = "-1") int city,
            @RequestParam(value = "id", defaultValue = "-1") int id) {
        if (city == -1 || id == -1) {
            return new UniversalDTO<>(true, "Неверные входные параметры", null);
        }
        UniversalDTO<Sale> result;
        Sale sale = saleRepository.getWithId(city, id);
        if (sale == null) {
            result = new UniversalDTO<>(true, "Акция не найдена", null);
        } else {
            result = new UniversalDTO<>(false, "", sale);
        }
        return result;
    }

    @RequestMapping("/comments")
    public UniversalDTO<Iterable<SaleComment>> getComments(@RequestParam(value = "sale_id", defaultValue = "") int saleId) {
        if (saleId == -1) {
            return new UniversalDTO<>(true, "Неверные входные параметры", null);
        }
        UniversalDTO<Iterable<SaleComment>> result = new UniversalDTO<>(true, "Ошибка при поиске комментариев", null);
        Iterable<SaleComment> comments = saleCommentRepository.getForSaleId(saleId);
        if (comments != null) {
            result = new UniversalDTO<>(false, "", comments);
        }
        return result;
    }

    @RequestMapping("/comments/new")
    public UniversalDTO<SaleComment> sendComment(
            @RequestParam(value = "author", defaultValue = "") String author,
            @RequestParam(value = "whom", defaultValue = "") String whom,
            @RequestParam(value = "text", defaultValue = "") String text,
            @RequestParam(value = "city", defaultValue = "-1") int cityId,
            @RequestParam(value = "sale_id", defaultValue = "-1") int saleId) {
        if (cityId == -1 || saleId == -1 || author.isEmpty() || text.isEmpty()) {
            return new UniversalDTO<>(true, "Не все поля заполнены", null);
        }
        Calendar currentDate = Calendar.getInstance();
        long dateTime = currentDate.getTimeInMillis();
        SaleComment saleComment = new SaleComment(saleId, author, whom, text, dateTime);
        saleCommentRepository.save(saleComment);
        /*APIFactory.getAPI().sendComment(author, text, cityId, saleId, 1)
                .subscribeOn(Schedulers.newThread())
                .subscribe(new Observer<SaleCommentDTO>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                    }

                    @Override
                    public void onNext(SaleCommentDTO saleCommentDTO) {

                    }
                });*/
        return new UniversalDTO<>(false, "", saleComment);
    }
}
