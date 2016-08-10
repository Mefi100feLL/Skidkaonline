package com.popcorp.parser.skidkaonline.error;

import com.popcorp.parser.skidkaonline.entity.Shop;

public class ShopNoFoundException extends Throwable {

    private Shop shop;

    public ShopNoFoundException(Shop shop, Throwable throwable){
        super(throwable);
        this.shop = shop;
    }

    public Shop getShop() {
        return shop;
    }

    public void setShop(Shop shop) {
        this.shop = shop;
    }
}
