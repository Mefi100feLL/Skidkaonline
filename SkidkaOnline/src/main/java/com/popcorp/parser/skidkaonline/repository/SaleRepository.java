package com.popcorp.parser.skidkaonline.repository;

import com.popcorp.parser.skidkaonline.entity.Sale;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.jdbc.support.rowset.SqlRowSet;

import java.sql.Types;
import java.util.ArrayList;

@org.springframework.stereotype.Repository(Sale.REPOSITORY)
public class SaleRepository implements DataRepository<Sale> {

    private static final String TABLE_SALES = "sales";
    private static final String TABLE_SALES_CITIES = "sales_cities";

    private static final String COLUMNS_SALE_ID = "sale_id";
    private static final String COLUMNS_CITY_URL = "city_url";
    private static final String COLUMNS_CITY_ID = "city_id";

    private static final String COLUMNS_SALES_CITIES = "(" +
            COLUMNS_SALE_ID + ", " +
            COLUMNS_CITY_ID + ", " +
            COLUMNS_CITY_URL + ")";

    private static final String COLUMNS_ID = "id";
    private static final String COLUMNS_SHOP_URL = "shop_url";
    private static final String COLUMNS_IMAGE_SMALL = "image_small";
    private static final String COLUMNS_IMAGE_BIG = "image_big";
    private static final String COLUMNS_PERIOD_START = "period_start";
    private static final String COLUMNS_PERIOD_END = "period_end";
    private static final String COLUMNS_CATALOG = "catalog";
    private static final String COLUMNS_IMAGE_WIDTH = "image_width";
    private static final String COLUMNS_IMAGE_HEIGHT = "image_height";

    private static final String COLUMNS_SALES = "(" +
            COLUMNS_ID + ", " +
            COLUMNS_SHOP_URL + ", " +
            COLUMNS_IMAGE_SMALL + ", " +
            COLUMNS_IMAGE_BIG + ", " +
            COLUMNS_PERIOD_START + ", " +
            COLUMNS_PERIOD_END + ", " +
            COLUMNS_CATALOG + ", " +
            COLUMNS_IMAGE_WIDTH + ", " +
            COLUMNS_IMAGE_HEIGHT + ")";

    private static final String COLUMNS_SALES_UPDATE =
            COLUMNS_SHOP_URL + "=?, " +
                    COLUMNS_IMAGE_SMALL + "=?, " +
                    COLUMNS_IMAGE_BIG + "=?, " +
                    COLUMNS_PERIOD_START + "=?, " +
                    COLUMNS_PERIOD_END + "=?, " +
                    COLUMNS_CATALOG + "=?, " +
                    COLUMNS_IMAGE_WIDTH + "=?, " +
                    COLUMNS_IMAGE_HEIGHT + "=?";

    @Autowired
    protected JdbcOperations jdbcOperations;

    @Override
    public int save(Sale object) {
        Object[] params = new Object[]{
                object.getId(),
                object.getShopUrl(),
                object.getImageSmall(),
                object.getImageBig(),
                object.getPeriodStart(),
                object.getPeriodEnd(),
                object.getCatalog(),
                object.getImageWidth(),
                object.getImageHeight()
        };
        int[] types = new int[]{
                Types.INTEGER,
                Types.VARCHAR,
                Types.VARCHAR,
                Types.VARCHAR,
                Types.BIGINT,
                Types.BIGINT,
                Types.VARCHAR,
                Types.INTEGER,
                Types.INTEGER,
        };

        int result = update(object);
        if (result == 0) {
            try {
                jdbcOperations.update("INSERT INTO " + TABLE_SALES + " " + COLUMNS_SALES + " VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?);", params, types);
            } catch (Exception ignored) {
            }
        }
        result = saveInCities(object.getId(), object.getCityId(), object.getCityUrl());
        return result;
    }

    private int saveInCities(int saleId, int cityId, String cityUrl) {
        try {
            return jdbcOperations.update("INSERT INTO " + TABLE_SALES_CITIES + " " + COLUMNS_SALES_CITIES + " VALUES (?, ?, ?);",
                    new Object[]{
                            saleId,
                            cityId,
                            cityUrl

                    }, new int[]{
                            Types.INTEGER,
                            Types.INTEGER,
                            Types.VARCHAR
                    });
        } catch (Exception e){
            return 1;
        }
    }


    @Override
    public int update(Sale object) {
        Object[] params = new Object[]{
                object.getShopUrl(),
                object.getImageSmall(),
                object.getImageBig(),
                object.getPeriodStart(),
                object.getPeriodEnd(),
                object.getCatalog(),
                object.getImageWidth(),
                object.getImageHeight(),
                object.getId()
        };

        return jdbcOperations.update("UPDATE " + TABLE_SALES + " SET " + COLUMNS_SALES_UPDATE + " WHERE " +
                COLUMNS_ID + "=? ;", params);
    }

    @Override
    public int save(Iterable<Sale> objects) {
        int count = 0;
        for (Sale sale : objects) {
            count += save(sale);
        }
        return count;
    }

    @Override
    public Iterable<Sale> getAll() {
        ArrayList<Sale> result = new ArrayList<>();
        SqlRowSet rowSet = jdbcOperations.queryForRowSet("SELECT * FROM " + TABLE_SALES + " INNER JOIN " + TABLE_SALES_CITIES + " ON " + COLUMNS_ID + "=" + COLUMNS_SALE_ID + ";");
        while (rowSet.next()) {
            Sale sale = getSale(rowSet);
            result.add(sale);
        }
        return result;
    }

    private Sale getSale(SqlRowSet rowSet) {
        return new Sale(
                rowSet.getInt(COLUMNS_ID),
                rowSet.getString(COLUMNS_SHOP_URL),
                rowSet.getString(COLUMNS_IMAGE_SMALL),
                rowSet.getString(COLUMNS_IMAGE_BIG),
                rowSet.getLong(COLUMNS_PERIOD_START),
                rowSet.getLong(COLUMNS_PERIOD_END),
                rowSet.getString(COLUMNS_CATALOG),
                rowSet.getString(COLUMNS_CITY_URL),
                rowSet.getInt(COLUMNS_CITY_ID),
                rowSet.getInt(COLUMNS_IMAGE_WIDTH),
                rowSet.getInt(COLUMNS_IMAGE_HEIGHT)
        );
    }

    public Iterable<Sale> getForShop(int cityId, String shop) {
        ArrayList<Sale> result = new ArrayList<>();
        SqlRowSet rowSet = jdbcOperations.queryForRowSet("SELECT * FROM " + TABLE_SALES + " INNER JOIN " + TABLE_SALES_CITIES + " ON " + COLUMNS_ID + "=" + COLUMNS_SALE_ID +
                " WHERE " + COLUMNS_CITY_ID + "=" + cityId + " AND " + COLUMNS_SHOP_URL + "='" + shop + "';");
        while (rowSet.next()) {
            Sale sale = getSale(rowSet);
            result.add(sale);
        }
        return result;
    }

    public Sale getWithId(int cityId, int id) {
        SqlRowSet rowSet = jdbcOperations.queryForRowSet("SELECT * FROM " + TABLE_SALES + " INNER JOIN " + TABLE_SALES_CITIES + " ON " + COLUMNS_ID + "=" + COLUMNS_SALE_ID +
                " WHERE " + COLUMNS_CITY_URL + "=" + cityId + " AND " + COLUMNS_ID + "=" + id + ";");
        if (rowSet.next()) {
            return getSale(rowSet);
        }
        return null;
    }
}
