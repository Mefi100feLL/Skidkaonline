package com.popcorp.parser.skidkaonline.repository;

import com.popcorp.parser.skidkaonline.entity.Category;
import com.popcorp.parser.skidkaonline.util.ErrorManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.jdbc.support.rowset.SqlRowSet;

import java.sql.Types;
import java.util.ArrayList;

@org.springframework.stereotype.Repository(Category.REPOSITORY)
public class CategoryRepository implements DataRepository<Category> {

    private static final String TABLE = "categories";

    private static final String COLUMN_NAME = "name";
    private static final String COLUMN_URL = "url";

    private static final String[] COLUMNS = new String[]{
            COLUMN_NAME,
            COLUMN_URL
    };

    @Autowired
    protected JdbcOperations jdbcOperations;

    @Autowired
    @Qualifier(Category.CITY_REPOSITORY)
    private CategoryCityRepository categoryCityRepository;


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

        int result = update(object);
        if (result == 0) {
            result = DB.insert(jdbcOperations, TABLE, COLUMNS, params, types);
        }

        categoryCityRepository.save(object);
        return result;
    }

    @Override
    public int update(Category object) {
        Object[] params = new Object[]{
                object.getName(),
                object.getUrl()
        };

        return DB.update(jdbcOperations, TABLE, new String[]{COLUMN_NAME}, new String[]{COLUMN_URL}, params);
    }

    @Override
    public int remove(Category object) {
        // Если такая категория для многих городов
        if (categoryCityRepository.getCount(object) > 1) {
            //удаляем только для текущего города
            return categoryCityRepository.remove(object);
        } else {
            //удаляем категорию полностью, из городов удалится каскадно
            return DB.remove(jdbcOperations, TABLE, new String[]{COLUMN_URL}, new Object[]{object.getUrl()});
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
        SqlRowSet rowSet = DB.getAllWithInnerJoin(jdbcOperations, TABLE, CategoryCityRepository.TABLE, COLUMN_URL, CategoryCityRepository.COLUMN_CATEGORY_URL);
        while (rowSet.next()) {
            result.add(getCategory(rowSet));
        }
        return result;
    }

    public Iterable<Category> getForCity(int cityId) {
        ArrayList<Category> result = new ArrayList<>();
        try {
            SqlRowSet rowSet = DB.getWithInnerJoin(jdbcOperations, TABLE, CategoryCityRepository.TABLE, COLUMN_URL, CategoryCityRepository.COLUMN_CATEGORY_URL, new String[]{CategoryCityRepository.COLUMN_CITY_ID}, new Object[]{cityId});
            if (rowSet != null) {
                while (rowSet.next()) {
                    result.add(getCategory(rowSet));
                }
            }
        } catch (Exception e) {
            ErrorManager.sendError(e.getMessage());
            e.printStackTrace();
            return null;
        }
        return result;
    }

    private Category getCategory(SqlRowSet rowSet) {
        return new Category(
                rowSet.getString(COLUMN_NAME),
                rowSet.getString(COLUMN_URL),
                rowSet.getString(CategoryCityRepository.COLUMN_CITY_URL),
                rowSet.getInt(CategoryCityRepository.COLUMN_CITY_ID)
        );
    }

    public Category getWithUrl(String url) {
        Category result = null;
        SqlRowSet rowSet = DB.getWithInnerJoin(jdbcOperations, TABLE, CategoryCityRepository.TABLE, COLUMN_URL, CategoryCityRepository.COLUMN_CATEGORY_URL, new String[]{COLUMN_URL}, new Object[]{url});
        if (rowSet != null && rowSet.next()) {
            result = getCategory(rowSet);
        }
        return result;
    }

    public Category getWithUrlAndCityId(String url, int cityId) {
        Category result = null;
        String[] selectionColumns = new String[]{
                COLUMN_URL,
                CategoryCityRepository.COLUMN_CITY_ID
        };
        Object[] selectionValues = new Object[]{
                url,
                cityId
        };
        SqlRowSet rowSet = DB.getWithInnerJoin(jdbcOperations, TABLE, CategoryCityRepository.TABLE, COLUMN_URL, CategoryCityRepository.COLUMN_CATEGORY_URL, selectionColumns, selectionValues);
        if (rowSet != null && rowSet.next()) {
            result = getCategory(rowSet);
        }
        return result;
    }

    public Category getWithNameAndCityId(String name, int cityId) {
        Category result = null;
        String[] selectionColumns = new String[]{
                COLUMN_NAME,
                CategoryCityRepository.COLUMN_CITY_ID
        };
        Object[] selectionValues = new Object[]{
                name,
                cityId
        };
        SqlRowSet rowSet = DB.getWithInnerJoin(jdbcOperations, TABLE, CategoryCityRepository.TABLE, COLUMN_URL, CategoryCityRepository.COLUMN_CATEGORY_URL, selectionColumns, selectionValues);
        if (rowSet != null && rowSet.next()) {
            result = getCategory(rowSet);
        }
        return result;
    }
}
