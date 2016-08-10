package com.popcorp.parser.skidkaonline.repository;

import com.popcorp.parser.skidkaonline.entity.Category;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcOperations;

import java.sql.Types;

@org.springframework.stereotype.Repository(Category.CITY_REPOSITORY)
public class CategoryCityRepository implements DataRepository<Category> {

    public static final String TABLE = "city_categories";

    public static final String COLUMN_CITY_URL = "city_url";
    public static final String COLUMN_CITY_ID = "city_id";
    public static final String COLUMN_CATEGORY_URL = "category_url";

    private static final String[] COLUMNS = new String[]{
            COLUMN_CITY_URL,
            COLUMN_CITY_ID,
            COLUMN_CATEGORY_URL
    };

    @Autowired
    protected JdbcOperations jdbcOperations;


    @Override
    public int save(Category object) {
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

        int result = update(object);
        if (result == 0) {
            result = DB.insert(jdbcOperations, TABLE, COLUMNS, params, types);
        }

        return result;
    }

    @Override
    public int update(Category object) {
        Object[] params = new Object[]{
                object.getCityUrl(),
                object.getCityId(),
                object.getUrl(),
                object.getCityUrl(),
                object.getCityId(),
                object.getUrl()
        };

        String[] setColumns = new String[] {
                COLUMN_CITY_URL,
                COLUMN_CITY_ID,
                COLUMN_CATEGORY_URL
        };
        String[] selectionColumns = new String[] {
                COLUMN_CITY_URL,
                COLUMN_CITY_ID,
                COLUMN_CATEGORY_URL
        };
        return DB.update(jdbcOperations, TABLE, setColumns, selectionColumns, params);
    }

    @Override
    public int remove(Category object) {
        String[] selectionColumns = new String[] {
                COLUMN_CITY_URL,
                COLUMN_CITY_ID,
                COLUMN_CATEGORY_URL
        };
        Object[] selectionValues = new Object[] {
                object.getCityUrl(),
                object.getCityId(),
                object.getUrl()
        };
        return DB.remove(jdbcOperations, TABLE, selectionColumns, selectionValues);
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
        return null;
    }

    public int getCount(Category category) {
        String[] selectionColumns = new String[] {
                COLUMN_CATEGORY_URL,
                COLUMN_CITY_ID
        };
        Object[] selectionValues = new Object[] {
                category.getUrl(),
                category.getCityId()
        };
        return DB.getCount(jdbcOperations, TABLE, selectionColumns, selectionValues);
    }
}
