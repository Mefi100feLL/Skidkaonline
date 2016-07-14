package com.popcorp.parser.skidkaonline.repository;

import com.popcorp.parser.skidkaonline.entity.Category;
import com.popcorp.parser.skidkaonline.entity.Shop;
import com.popcorp.parser.skidkaonline.util.ErrorManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.jdbc.support.rowset.SqlRowSet;

import java.sql.Types;
import java.util.ArrayList;

@org.springframework.stereotype.Repository(Shop.REPOSITORY)
public class ShopsRepository implements DataRepository<Shop> {

    private static final String TABLE_SHOPS = "shops";

    private static final String COLUMNS_NAME = "name";
    private static final String COLUMNS_URL = "url";
    private static final String COLUMNS_IMAGE = "image";

    private static final String COLUMNS_SHOPS = "(" +
            COLUMNS_NAME + ", " +
            COLUMNS_URL + ", " +
            COLUMNS_IMAGE + ")";

    private static final String COLUMNS_SHOPS_UPDATE =
            COLUMNS_NAME + "=?, " +
                    COLUMNS_IMAGE + "=?";


    @Autowired
    protected JdbcOperations jdbcOperations;

    @Autowired
    @Qualifier(Category.REPOSITORY)
    private CategoryRepository categoryRepository;


    @Override
    public int save(Shop object) {
        Object[] params = new Object[]{
                object.getName(),
                object.getUrl(),
                object.getImage()
        };
        int[] types = new int[]{
                Types.VARCHAR,
                Types.VARCHAR,
                Types.VARCHAR
        };

        int result;
        int countOfUpdated = update(object);
        if (countOfUpdated == 0) {
            try {
                result = jdbcOperations.update("INSERT INTO " + TABLE_SHOPS + " " + COLUMNS_SHOPS + " VALUES (?, ?, ?);", params, types);
            } catch (Exception e) {
                result = 1;
            }
        } else {
            result = countOfUpdated;
        }
        saveInCities(object);
        return result;
    }

    @Override
    public int update(Shop object) {
        Object[] params = new Object[]{
                object.getName(),
                object.getImage(),
                object.getUrl()
        };

        return jdbcOperations.update("UPDATE " + TABLE_SHOPS + " SET " + COLUMNS_SHOPS_UPDATE + " WHERE " +
                COLUMNS_URL + "=?;", params);
    }


    private static final String TABLE_SHOPS_FOR_CATEG_CITIES = "shops_categs_cities";

    private static final String COLUMNS_SHOP_URL = "shop_url";
    private static final String COLUMNS_CATEGORY_URL = "category_url";
    private static final String COLUMNS_CITY_ID = "city_id";
    private static final String COLUMNS_CITY_URL = "city_url";

    private static final String COLUMNS_SHOPS_FOR_CATEG_CITIES = "(" +
            COLUMNS_SHOP_URL + ", " +
            COLUMNS_CATEGORY_URL + ", " +
            COLUMNS_CITY_URL + ", " +
            COLUMNS_CITY_ID + ")";

    private int saveInCities(Shop object) {
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

        try {
            return jdbcOperations.update("INSERT INTO " + TABLE_SHOPS_FOR_CATEG_CITIES + " " + COLUMNS_SHOPS_FOR_CATEG_CITIES + " VALUES (?, ?, ?, ?);", params, types);
        } catch (Exception e) {
            return 1;
        }
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
        ArrayList<Shop> result = new ArrayList<>();
        SqlRowSet rowSet = jdbcOperations.queryForRowSet("SELECT * FROM " + TABLE_SHOPS + " INNER JOIN " + TABLE_SHOPS_FOR_CATEG_CITIES + " ON " + COLUMNS_URL + "=" + COLUMNS_SHOP_URL + ";");
        while (rowSet.next()) {
            result.add(getShop(rowSet));
        }
        return result;
    }

    public Iterable<Shop> getForCityAndCategory(int cityId, String category) {
        ArrayList<Shop> result = new ArrayList<>();
        SqlRowSet rowSet = jdbcOperations.queryForRowSet("SELECT * FROM " + TABLE_SHOPS + " INNER JOIN " + TABLE_SHOPS_FOR_CATEG_CITIES + " ON " + COLUMNS_URL + "=" + COLUMNS_SHOP_URL +
                " WHERE " + COLUMNS_CITY_ID + "=" + cityId + " AND " + COLUMNS_CATEGORY_URL + "='" + category + "';");
        while (rowSet.next()) {
            result.add(getShop(rowSet));
        }
        return result;
    }

    private Shop getShop(SqlRowSet rowSet) {
        return new Shop(
                rowSet.getString(COLUMNS_NAME),
                rowSet.getString(COLUMNS_IMAGE),
                rowSet.getString(COLUMNS_URL),
                categoryRepository.getWithUrlAndCityId(rowSet.getString(COLUMNS_CATEGORY_URL), rowSet.getInt(COLUMNS_CITY_ID)),
                rowSet.getString(COLUMNS_CITY_URL),
                rowSet.getInt(COLUMNS_CITY_ID)
        );
    }

    public Iterable<Shop> getForCity(int cityId) {
        ArrayList<Shop> result = new ArrayList<>();
        try {
            SqlRowSet rowSet = jdbcOperations.queryForRowSet("SELECT * FROM " + TABLE_SHOPS + " INNER JOIN " + TABLE_SHOPS_FOR_CATEG_CITIES + " ON " + COLUMNS_URL + "=" + COLUMNS_SHOP_URL +
                    " WHERE " + COLUMNS_CITY_ID + "=" + cityId + ";");
            while (rowSet.next()) {
                result.add(getShop(rowSet));
            }
        } catch (Exception e) {
            ErrorManager.sendError(e.getMessage());
            e.printStackTrace();
            return null;
        }
        return result;
    }
}
