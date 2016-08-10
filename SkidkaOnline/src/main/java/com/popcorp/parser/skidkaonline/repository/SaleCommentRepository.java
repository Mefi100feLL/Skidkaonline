package com.popcorp.parser.skidkaonline.repository;

import com.popcorp.parser.skidkaonline.entity.SaleComment;
import com.popcorp.parser.skidkaonline.util.ErrorManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.jdbc.support.rowset.SqlRowSet;

import java.sql.Types;
import java.util.ArrayList;

@org.springframework.stereotype.Repository(SaleComment.REPOSITORY)
public class SaleCommentRepository implements DataRepository<SaleComment> {

    private static final String TABLE = "sales_comments";

    private static final String COLUMN_SALE_ID = "sale_id";
    private static final String COLUMN_AUTHOR = "author";
    private static final String COLUMN_WHOM = "whom";
    private static final String COLUMN_TEXT = "text";
    private static final String COLUMN_DATE_TIME = "date_time";

    private static final String[] COLUMNS = new String[]{
            COLUMN_SALE_ID,
            COLUMN_AUTHOR,
            COLUMN_WHOM,
            COLUMN_TEXT,
            COLUMN_DATE_TIME
    };

    @Autowired
    protected JdbcOperations jdbcOperations;

    @Override
    public int save(SaleComment object) {
        Object[] params = new Object[]{
                object.getSaleId(),
                object.getAuthor(),
                object.getWhom(),
                object.getText(),
                object.getDateTime()};
        int[] types = new int[]{
                Types.INTEGER,
                Types.VARCHAR,
                Types.VARCHAR,
                Types.VARCHAR,
                Types.BIGINT};

        int result = update(object);
        if (result == 0) {
            result = DB.insert(jdbcOperations, TABLE, COLUMNS, params, types);
        }

        return result;
    }

    @Override
    public int update(SaleComment object) {
        Object[] params = new Object[]{
                object.getWhom(),
                object.getText(),
                object.getSaleId(),
                object.getAuthor(),
                object.getDateTime()};

        String[] setColumns = new String[]{
                COLUMN_WHOM,
                COLUMN_TEXT
        };
        String[] selectionColumns = new String[]{
                COLUMN_SALE_ID,
                COLUMN_AUTHOR,
                COLUMN_DATE_TIME
        };
        return DB.update(jdbcOperations, TABLE, setColumns, selectionColumns, params);
    }

    @Override
    public int remove(SaleComment object) {
        String[] selectionColumns = new String[]{
                COLUMN_SALE_ID,
                COLUMN_AUTHOR,
                COLUMN_DATE_TIME
        };
        Object[] selectionValues = new Object[]{
                object.getSaleId(),
                object.getAuthor(),
                object.getDateTime()
        };
        return DB.remove(jdbcOperations, TABLE, selectionColumns, selectionValues);
    }

    @Override
    public int save(Iterable<SaleComment> objects) {
        int count = 0;
        for (SaleComment saleComment : objects) {
            count += save(saleComment);
        }
        return count;
    }

    @Override
    public Iterable<SaleComment> getAll() {
        ArrayList<SaleComment> result = new ArrayList<>();
        SqlRowSet rowSet = DB.getAll(jdbcOperations, TABLE);
        if (rowSet != null) {
            while (rowSet.next()) {
                result.add(getSaleComment(rowSet));
            }
        }
        return result;
    }

    public Iterable<SaleComment> getForSaleId(int saleId) {
        ArrayList<SaleComment> result = new ArrayList<>();
        try {
            SqlRowSet rowSet = DB.get(jdbcOperations, TABLE, new String[]{COLUMN_SALE_ID}, new Object[]{saleId});
            if (rowSet != null) {
                while (rowSet.next()) {
                    result.add(getSaleComment(rowSet));
                }
            }
        } catch (Exception e) {
            ErrorManager.sendError(e.getMessage());
            e.printStackTrace();
            return null;
        }
        return result;
    }

    private SaleComment getSaleComment(SqlRowSet rowSet) {
        return new SaleComment(
                rowSet.getInt(COLUMN_SALE_ID),
                rowSet.getString(COLUMN_AUTHOR),
                rowSet.getString(COLUMN_WHOM),
                rowSet.getString(COLUMN_TEXT),
                rowSet.getLong(COLUMN_DATE_TIME));
    }

    public int getCount(int saleId) {
        return DB.getCount(jdbcOperations, TABLE, new String[]{COLUMN_SALE_ID}, new Object[]{saleId});
    }
}
