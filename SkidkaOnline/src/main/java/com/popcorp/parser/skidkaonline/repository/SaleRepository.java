package com.popcorp.parser.skidkaonline.repository;

import com.popcorp.parser.skidkaonline.entity.Sale;
import com.popcorp.parser.skidkaonline.entity.SaleComment;
import com.popcorp.parser.skidkaonline.util.ErrorManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.jdbc.support.rowset.SqlRowSet;

import java.sql.Types;
import java.util.ArrayList;

@org.springframework.stereotype.Repository(Sale.REPOSITORY)
public class SaleRepository implements DataRepository<Sale> {

    private static final String TABLE = "sales";

    private static final String COLUMN_ID = "id";
    private static final String COLUMN_SHOP_URL = "shop_url";
    private static final String COLUMN_IMAGE_SMALL = "image_small";
    private static final String COLUMN_IMAGE_BIG = "image_big";
    private static final String COLUMN_PERIOD_START = "period_start";
    private static final String COLUMN_PERIOD_END = "period_end";
    private static final String COLUMN_CATALOG = "catalog";
    private static final String COLUMN_IMAGE_WIDTH = "image_width";
    private static final String COLUMN_IMAGE_HEIGHT = "image_height";

    private static final String[] COLUMNS = new String[]{
            COLUMN_ID,
            COLUMN_SHOP_URL,
            COLUMN_IMAGE_SMALL,
            COLUMN_IMAGE_BIG,
            COLUMN_PERIOD_START,
            COLUMN_PERIOD_END,
            COLUMN_CATALOG,
            COLUMN_IMAGE_WIDTH,
            COLUMN_IMAGE_HEIGHT
    };

    @Autowired
    protected JdbcOperations jdbcOperations;

    @Autowired
    @Qualifier(SaleComment.REPOSITORY)
    private SaleCommentRepository saleCommentRepository;

    @Autowired
    @Qualifier(Sale.CITY_REPOSITORY)
    private SaleCityRepository saleCityRepository;


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
            result = DB.insert(jdbcOperations, TABLE, COLUMNS, params, types);
        }

        saleCityRepository.save(object);
        return result;
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

        String[] setColumns = new String[]{
                COLUMN_SHOP_URL,
                COLUMN_IMAGE_SMALL,
                COLUMN_IMAGE_BIG,
                COLUMN_PERIOD_START,
                COLUMN_PERIOD_END,
                COLUMN_CATALOG,
                COLUMN_IMAGE_WIDTH,
                COLUMN_IMAGE_HEIGHT,
        };
        String[] selectionColumns = new String[]{COLUMN_ID};
        return DB.update(jdbcOperations, TABLE, setColumns, selectionColumns, params);
    }

    @Override
    public int remove(Sale object) {
        // Если такая акция для многих городов
        if (saleCityRepository.getCount(object.getId()) > 1) {
            //удаляем только для текущего города
            return saleCityRepository.remove(object);
        } else {
            //удаляем акцию полностью, комменты и из городов удалятся каскадно
            return DB.remove(jdbcOperations, TABLE, new String[]{COLUMN_ID}, new Object[]{object.getId()});
        }
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
        SqlRowSet rowSet = DB.getAllWithInnerJoin(jdbcOperations, TABLE, SaleCityRepository.TABLE, COLUMN_ID, SaleCityRepository.COLUMN_SALE_ID);
        if (rowSet != null) {
            while (rowSet.next()) {
                Sale sale = getSale(rowSet);
                result.add(sale);
            }
        }
        return result;
    }

    private Sale getSale(SqlRowSet rowSet) {
        Sale result = new Sale(
                rowSet.getInt(COLUMN_ID),
                rowSet.getString(COLUMN_SHOP_URL),
                rowSet.getString(COLUMN_IMAGE_SMALL),
                rowSet.getString(COLUMN_IMAGE_BIG),
                rowSet.getLong(COLUMN_PERIOD_START),
                rowSet.getLong(COLUMN_PERIOD_END),
                rowSet.getString(COLUMN_CATALOG),
                rowSet.getString(SaleCityRepository.COLUMN_CITY_URL),
                rowSet.getInt(SaleCityRepository.COLUMN_CITY_ID),
                rowSet.getInt(COLUMN_IMAGE_WIDTH),
                rowSet.getInt(COLUMN_IMAGE_HEIGHT)
        );
        result.setCountComments(saleCommentRepository.getCount(result.getId()));
        return result;
    }

    public Iterable<Sale> getForShop(int cityId, String shop) {
        ArrayList<Sale> result = new ArrayList<>();
        try {
            String[] selectionColumns = new String[]{
                    SaleCityRepository.COLUMN_CITY_ID,
                    COLUMN_SHOP_URL
            };
            Object[] selectionValues = new Object[]{
                    cityId,
                    shop
            };
            SqlRowSet rowSet = DB.getWithInnerJoin(jdbcOperations, TABLE, SaleCityRepository.TABLE, COLUMN_ID, SaleCityRepository.COLUMN_SALE_ID, selectionColumns, selectionValues);
            if (rowSet != null) {
                while (rowSet.next()) {
                    Sale sale = getSale(rowSet);
                    result.add(sale);
                }
            }
        } catch (Exception e) {
            ErrorManager.sendError(e.getMessage());
            e.printStackTrace();
            return null;
        }
        return result;
    }

    public Sale getWithId(int cityId, int id) {
        try {
            String[] selectionColumns = new String[]{
                    SaleCityRepository.COLUMN_CITY_URL,
                    COLUMN_ID
            };
            Object[] selectionValues = new Object[]{
                    cityId,
                    id
            };
            SqlRowSet rowSet = DB.getWithInnerJoin(jdbcOperations, TABLE, SaleCityRepository.TABLE, COLUMN_ID, SaleCityRepository.COLUMN_SALE_ID, selectionColumns, selectionValues);
            if (rowSet != null && rowSet.next()) {
                return getSale(rowSet);
            }
        } catch (Exception e) {
            ErrorManager.sendError(e.getMessage());
            e.printStackTrace();
            return null;
        }
        return null;
    }
}
