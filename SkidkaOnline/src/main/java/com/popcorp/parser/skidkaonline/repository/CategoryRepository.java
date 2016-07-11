package com.popcorp.parser.skidkaonline.repository;

import com.popcorp.parser.skidkaonline.entity.Category;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.jdbc.support.rowset.SqlRowSet;

import java.sql.Types;
import java.util.ArrayList;

@org.springframework.stereotype.Repository(Category.REPOSITORY)
public class CategoryRepository implements DataRepository<Category> {

    private static final String TABLE_CATEGORIES = "categories";

    private static final String COLUMNS_NAME = "name";
    private static final String COLUMNS_URL = "url";

    private static final String COLUMNS_CATEGORIES = "(" + COLUMNS_NAME + ", " + COLUMNS_URL + ")";

    private static final String COLUMNS_CATEGORIES_UPDATE = COLUMNS_NAME + "=?";

    @Autowired
    protected JdbcOperations jdbcOperations;

    @Override
    public int save(Category object) {
        Object[] params = new Object[]{
                object.getName(),
                object.getUrl()
        };
        int[] types = new int[]{
                Types.VARCHAR,
                Types.VARCHAR
        };

        int result;
        int countOfUpdated = update(object);
        if (countOfUpdated == 0) {
            try {
                result = jdbcOperations.update("INSERT INTO " + TABLE_CATEGORIES + " " + COLUMNS_CATEGORIES + " VALUES (?, ?);", params, types);
            } catch (Exception e){
                result = 1;
            }
        } else {
            result = countOfUpdated;
        }

        saveInCities(object);
        return result;
    }

    @Override
    public int update(Category object) {
        Object[] params = new Object[]{
                object.getName(),
                object.getUrl()
        };

        return jdbcOperations.update("UPDATE " + TABLE_CATEGORIES + " SET " + COLUMNS_CATEGORIES_UPDATE + " WHERE " +
                COLUMNS_URL + "=?;", params);
    }


    private static final String TABLE_CATEGORIES_FOR_CITIES = "city_categories";

    private static final String COLUMNS_CITY_URL = "city_url";
    private static final String COLUMNS_CITY_ID = "city_id";
    private static final String COLUMNS_CATEGORY_URL = "category_url";


    private static final String COLUMNS_CATEGORIES_WITH_CITIES = "(" + COLUMNS_CITY_URL + ", " + COLUMNS_CITY_ID + ", " + COLUMNS_CATEGORY_URL + ")";


    private int saveInCities(Category object) {
        Object[] params = new Object[]{
                object.getCityUrl(),
                object.getCityId(),
                object.getUrl()
        };
        int[] types = new int[]{
                Types.VARCHAR,
                Types.INTEGER,
                Types.VARCHAR
        };

        try {
            return jdbcOperations.update("INSERT INTO " + TABLE_CATEGORIES_FOR_CITIES + " " + COLUMNS_CATEGORIES_WITH_CITIES + " VALUES (?, ?, ?);", params, types);
        } catch (Exception e) {
            return 1;
        }
    }

    @Override
    public int save(Iterable<Category> objects) {
        int count = 0;
        for (Category category : objects) {
            count += save(category);
        }
        return count;
    }

    @Override
    public Iterable<Category> getAll() {
        ArrayList<Category> result = new ArrayList<>();
        SqlRowSet rowSet = jdbcOperations.queryForRowSet("SELECT * FROM " + TABLE_CATEGORIES + " INNER JOIN " + TABLE_CATEGORIES_FOR_CITIES + " ON " + COLUMNS_URL + "=" + COLUMNS_CATEGORY_URL + ";");
        while (rowSet.next()) {
            result.add(getCategory(rowSet));
        }
        return result;
    }

    public Iterable<Category> getForCity(int cityId) {
        ArrayList<Category> result = new ArrayList<>();
        SqlRowSet rowSet = jdbcOperations.queryForRowSet("SELECT * FROM " + TABLE_CATEGORIES + " INNER JOIN " + TABLE_CATEGORIES_FOR_CITIES +
                " ON " + COLUMNS_URL + "=" + COLUMNS_CATEGORY_URL + " WHERE " + COLUMNS_CITY_ID + "=" + cityId + ";");
        while (rowSet.next()) {
            result.add(getCategory(rowSet));
        }
        return result;
    }

    private Category getCategory(SqlRowSet rowSet){
        return new Category(
                rowSet.getString(COLUMNS_NAME),
                rowSet.getString(COLUMNS_URL),
                rowSet.getString(COLUMNS_CITY_URL),
                rowSet.getInt(COLUMNS_CITY_ID)
        );
    }

    public Category getWithUrl(String url) {
        Category result = null;
        SqlRowSet rowSet = jdbcOperations.queryForRowSet("SELECT * FROM " + TABLE_CATEGORIES + " INNER JOIN " + TABLE_CATEGORIES_FOR_CITIES +
                " ON " + COLUMNS_URL + "=" + COLUMNS_CATEGORY_URL + " WHERE " + COLUMNS_URL + "='" + url + "';");
        if (rowSet.next()) {
            result = getCategory(rowSet);
        }
        return result;
    }

    public Category getWithUrlAndCityId(String url, int cityId) {
        Category result = null;
        SqlRowSet rowSet = jdbcOperations.queryForRowSet("SELECT * FROM " + TABLE_CATEGORIES + " INNER JOIN " + TABLE_CATEGORIES_FOR_CITIES +
                " ON " + COLUMNS_URL + "=" + COLUMNS_CATEGORY_URL + " WHERE " + COLUMNS_URL + "='" + url + "' AND " + COLUMNS_CITY_ID + "=" + cityId + ";");
        if (rowSet.next()) {
            result = getCategory(rowSet);
        }
        return result;
    }

    public Category getWithNameAndCityId(String name, int cityId) {
        Category result = null;
        SqlRowSet rowSet = jdbcOperations.queryForRowSet("SELECT * FROM " + TABLE_CATEGORIES + " INNER JOIN " + TABLE_CATEGORIES_FOR_CITIES +
                " ON " + COLUMNS_URL + "=" + COLUMNS_CATEGORY_URL + " WHERE " + COLUMNS_NAME + "='" + name + "' AND " + COLUMNS_CITY_ID + "=" + cityId + ";");
        if (rowSet.next()) {
            result = getCategory(rowSet);
        }
        return result;
    }
}
