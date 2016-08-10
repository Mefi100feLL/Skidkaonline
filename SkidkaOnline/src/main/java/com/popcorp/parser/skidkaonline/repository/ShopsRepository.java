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

    private static final String TABLE = "shops";

    private static final String COLUMN_NAME = "name";
    private static final String COLUMN_URL = "url";
    private static final String COLUMN_IMAGE = "image";

    private static final String[] COLUMNS = new String[]{
            COLUMN_NAME,
            COLUMN_URL,
            COLUMN_IMAGE
    };

    @Autowired
    protected JdbcOperations jdbcOperations;

    @Autowired
    @Qualifier(Category.REPOSITORY)
    private CategoryRepository categoryRepository;

    @Autowired
    @Qualifier(Shop.CITY_REPOSITORY)
    private ShopCityRepository shopCityRepository;


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

        int result = update(object);
        if (result == 0) {
            result = DB.insert(jdbcOperations, TABLE, COLUMNS, params, types);
        }

        shopCityRepository.save(object);
        return result;
    }

    @Override
    public int update(Shop object) {
        Object[] params = new Object[]{
                object.getName(),
                object.getImage(),
                object.getUrl()
        };

        String[] setColumns = new String[] {
                COLUMN_NAME,
                COLUMN_IMAGE
        };
        String[] selectionColumns = new String[] {COLUMN_URL};
        return DB.update(jdbcOperations, TABLE, setColumns, selectionColumns, params);
    }

    @Override
    public int remove(Shop object) {
        // Если такой магазин для многих городов
        if (shopCityRepository.getCount(object) > 1) {
            //удаляем только для текущего города
            return shopCityRepository.remove(object);
        } else {
            //удаляем магазин полностью, из городов удалится каскадно
            return DB.remove(jdbcOperations, TABLE, new String[] {COLUMN_URL}, new Object[] {object.getUrl()});
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
        SqlRowSet rowSet = DB.getAllWithInnerJoin(jdbcOperations, TABLE, ShopCityRepository.TABLE, COLUMN_URL, ShopCityRepository.COLUMN_SHOP_URL);
        if (rowSet != null) {
            while (rowSet.next()) {
                result.add(getShop(rowSet));
            }
        }
        return result;
    }

    public Iterable<Shop> getForCityAndCategory(int cityId, String category) {
        ArrayList<Shop> result = new ArrayList<>();
        String[] selectionColumns = new String[] {
                ShopCityRepository.COLUMN_CITY_ID,
                ShopCityRepository.COLUMN_CATEGORY_URL
        };
        Object[] selectionValues = new Object[] {
                cityId,
                category
        };
        SqlRowSet rowSet = DB.getWithInnerJoin(jdbcOperations, TABLE, ShopCityRepository.TABLE, COLUMN_URL, ShopCityRepository.COLUMN_SHOP_URL, selectionColumns, selectionValues);
        if (rowSet != null) {
            while (rowSet.next()) {
                result.add(getShop(rowSet));
            }
        }
        return result;
    }

    private Shop getShop(SqlRowSet rowSet) {
        return new Shop(
                rowSet.getString(COLUMN_NAME),
                rowSet.getString(COLUMN_IMAGE),
                rowSet.getString(COLUMN_URL),
                categoryRepository.getWithUrlAndCityId(rowSet.getString(ShopCityRepository.COLUMN_CATEGORY_URL), rowSet.getInt(ShopCityRepository.COLUMN_CITY_ID)),
                rowSet.getString(ShopCityRepository.COLUMN_CITY_URL),
                rowSet.getInt(ShopCityRepository.COLUMN_CITY_ID)
        );
    }

    public Iterable<Shop> getForCity(int cityId) {
        ArrayList<Shop> result = new ArrayList<>();
        try {
            String[] selectionColumns = new String[] {
                    ShopCityRepository.COLUMN_CITY_ID
            };
            Object[] selectionValues = new Object[] {
                    cityId
            };
            SqlRowSet rowSet = DB.getWithInnerJoin(jdbcOperations, TABLE, ShopCityRepository.TABLE, COLUMN_URL, ShopCityRepository.COLUMN_SHOP_URL, selectionColumns, selectionValues);
            if (rowSet != null) {
                while (rowSet.next()) {
                    result.add(getShop(rowSet));
                }
            }
        } catch (Exception e) {
            ErrorManager.sendError(e.getMessage());
            e.printStackTrace();
            return null;
        }
        return result;
    }
}
