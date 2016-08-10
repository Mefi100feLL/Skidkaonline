package com.popcorp.parser.skidkaonline.repository;

import com.popcorp.parser.skidkaonline.entity.Sale;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcOperations;

import java.sql.Types;

@org.springframework.stereotype.Repository(Sale.CITY_REPOSITORY)
public class SaleCityRepository implements DataRepository<Sale> {

    public static final String TABLE = "sales_cities";

    public static final String COLUMN_SALE_ID = "sale_id";
    public static final String COLUMN_CITY_URL = "city_url";
    public static final String COLUMN_CITY_ID = "city_id";

    private static final String[] COLUMNS = new String[]{
            COLUMN_SALE_ID,
            COLUMN_CITY_ID,
            COLUMN_CITY_URL
    };

    @Autowired
    protected JdbcOperations jdbcOperations;


    @Override
    public int save(Sale object) {
        Object[] params = new Object[]{
                object.getId(),
                object.getCityId(),
                object.getCityUrl()
        };
        int[] types = new int[]{
                Types.INTEGER,
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
    public int update(Sale object) {
        Object[] params = new Object[]{
                object.getId(),
                object.getCityId(),
                object.getCityUrl(),
                object.getId(),
                object.getCityId(),
                object.getCityUrl()
        };

        String[] setColumns = new String[]{
                COLUMN_SALE_ID,
                COLUMN_CITY_ID,
                COLUMN_CITY_URL
        };
        String[] selectionColumns = new String[]{
                COLUMN_SALE_ID,
                COLUMN_CITY_ID,
                COLUMN_CITY_URL
        };
        return DB.update(jdbcOperations, TABLE, setColumns, selectionColumns, params);
    }

    @Override
    public int remove(Sale object) {
        String[] selectionColumns = new String[]{
                COLUMN_CITY_ID,
                COLUMN_SALE_ID
        };
        Object[] selectionValues = new Object[]{
                object.getCityId(),
                object.getId()
        };
        return DB.remove(jdbcOperations, TABLE, selectionColumns, selectionValues);
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
        return null;
    }

    public int getCount(int saleId) {
        return DB.getCount(jdbcOperations, TABLE, new String[]{COLUMN_SALE_ID}, new Object[]{saleId});
    }
}
