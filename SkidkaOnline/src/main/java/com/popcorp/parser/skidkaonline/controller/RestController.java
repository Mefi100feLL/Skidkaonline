package com.popcorp.parser.skidkaonline.controller;

import com.popcorp.parser.skidkaonline.dto.SaleCommentsDTO;
import com.popcorp.parser.skidkaonline.entity.*;
import com.popcorp.parser.skidkaonline.net.APIFactory;
import com.popcorp.parser.skidkaonline.repository.CategoryRepository;
import com.popcorp.parser.skidkaonline.repository.CityRepository;
import com.popcorp.parser.skidkaonline.repository.SaleRepository;
import com.popcorp.parser.skidkaonline.repository.ShopsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

import java.util.ArrayList;

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


    @RequestMapping("/cities")
    public Iterable<City> getCities() {
        return cityRepository.getAll();
    }

    @RequestMapping("/categories")
    public Iterable<Category> getCategories(@RequestParam(value = "city", defaultValue = "-1") int cityId) {
        return categoryRepository.getForCity(cityId);
    }

    @RequestMapping("/shops")
    public Iterable<Shop> getShopsForCity(@RequestParam(value = "city", defaultValue = "-1") int cityId) {
        return shopRepository.getForCity(cityId);
    }

    @RequestMapping("/sales")
    public Iterable<Sale> getSales(
            @RequestParam(value = "city", defaultValue = "-1") int cityId,
            @RequestParam(value = "shop", defaultValue = "") String shop) {
        return saleRepository.getForShop(cityId, shop);
    }

    @RequestMapping("/sale")
    public Sale getSale(
            @RequestParam(value = "city", defaultValue = "-1") int city,
            @RequestParam(value = "id", defaultValue = "-1") int id) {
        Sale result = null;
        if (id != -1 && city != -1) {
            result = saleRepository.getWithId(city, id);
        }
        return result;
    }

    @RequestMapping("/comments")
    public Iterable<SaleComment> getComments(@RequestParam(value = "sale_id", defaultValue = "") int saleId) {
        return APIFactory.getAPI().getComments(saleId, 1)
                .subscribeOn(Schedulers.newThread())
                .map((Func1<SaleCommentsDTO, Iterable<SaleComment>>) saleCommentsDTO -> {
                    ArrayList<SaleComment> result = new ArrayList<>();
                    if (!saleCommentsDTO.isError() && saleCommentsDTO.getComments() != null) {
                        result.addAll(saleCommentsDTO.getComments());
                    }
                    return result;
                })
                .toBlocking()
                .first();
    }

    @RequestMapping("/comments/new")
    public Result<SaleComment> sendComment(
            @RequestParam(value = "author", defaultValue = "") String author,
            @RequestParam(value = "text", defaultValue = "") String text,
            @RequestParam(value = "city_id", defaultValue = "-1") int cityId,
            @RequestParam(value = "sale_id", defaultValue = "-1") int saleId) {
        if (cityId == -1 || saleId == -1 || author.isEmpty() || text.isEmpty()) {
            return new Result<>(false, "Empty field", null);
        }
        return APIFactory.getAPI().sendComment(author, text, cityId, saleId, 1)
                .subscribeOn(Schedulers.newThread())
                .map(saleCommentDTO -> {
                    if (saleCommentDTO.isError()) {
                        return new Result<SaleComment>(false, "Any error", null);
                    } else{
                        SaleComment comment = saleCommentDTO.getComment();
                        comment.setSaleId(saleId);
                        return new Result<>(true, "", comment);
                    }
                })
                .toBlocking()
                .first();
    }
}
