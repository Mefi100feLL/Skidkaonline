package com.popcorp.parser.skidkaonline.repository;

import com.popcorp.parser.skidkaonline.entity.Shop;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcOperations;

import java.sql.Types;

@org.springframework.stereotype.Repository(Shop.CITY_REPOSITORY)
public class ShopCityRepository implements DataRepository<Shop> {

    public static final String TABLE = "shops_categs_cities";

    public static final String COLUMN_SHOP_URL = "shop_url";
    public static final String COLUMN_CATEGORY_URL = "category_url";
    public static final String COLUMN_CITY_ID = "city_id";
    public static final String COLUMN_CITY_URL = "city_url";

    private static final String[] COLUMNS = new String[]{
            COLUMN_SHOP_URL,
            COLUMN_CATEGORY_URL,
            COLUMN_CITY_URL,
            COLUMN_CITY_ID
    };

    @Autowired
    protected JdbcOperations jdbcOperations;


    @Override
    public int save(Shop object) {
        Object[] params = new Object[]{
                object.getUrl(),
                object.getCategory().getUrl(),
                object.getCityUrl(),
                object.getCityId()
        };
        int[] types = new int[]{
                Types.VARCHAR,
                Types.VARCHAR,
                Types.VARCHAR,
                Types.INTEGER
        };

        int result = update(object);
        if (result == 0) {
            result = DB.insert(jdbcOperations, TABLE, COLUMNS, params, types);
        }

        return result;
    }

    @Override
    public int update(Shop object) {
        Object[] params = new Object[]{
                object.getUrl(),
                object.getCategory().getUrl(),
                object.getCityUrl(),
                object.getCityId(),
                object.getUrl(),
                object.getCategory().getUrl(),
                object.getCityUrl(),
                object.getCityId()
        };

        String[] setColumns = new String[] {
                COLUMN_SHOP_URL,
                COLUMN_CATEGORY_URL,
                COLUMN_CITY_URL,
                COLUMN_CITY_ID
        };
        String[] selectionColumns = new String[] {
                COLUMN_SHOP_URL,
                COLUMN_CATEGORY_URL,
                COLUMN_CITY_URL,
                COLUMN_CITY_ID
        };
        return DB.update(jdbcOperations, TABLE, setColumns, selectionColumns, params);
    }

    @Override
    public int remove(Shop object) {
        String[] selectionColumns = new String[] {
                COLUMN_SHOP_URL,
                COLUMN_CITY_ID
        };
        Object[] selectionValues = new Object[] {
                object.getUrl(),
                object.getCityId()
        };
        return DB.remove(jdbcOperations, TABLE, selectionColumns, selectionValues);
    }

    @Override
    public int save(Iterable<Shop> objects) {
        int count = 0;
        for (Shop shop : objects) {
            count += save(shop);
        }
        return count;
    }

    @Override
    public Iterable<Shop> getAll() {
        return null;
    }

    public int getCount(Shop object) {
        String[] selectionColumns = new String[] {
                COLUMN_SHOP_URL,
                COLUMN_CITY_ID
        };
        Object[] selectionValues = new Object[] {
                object.getUrl(),
                object.getCityId()
        };
        return DB.getCount(jdbcOperations, TABLE, selectionColumns, selectionValues);
    }
}
