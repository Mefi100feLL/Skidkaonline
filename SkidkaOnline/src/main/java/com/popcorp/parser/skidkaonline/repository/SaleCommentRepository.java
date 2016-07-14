package com.popcorp.parser.skidkaonline.repository;

import com.popcorp.parser.skidkaonline.entity.Sale;
import com.popcorp.parser.skidkaonline.entity.SaleComment;
import com.popcorp.parser.skidkaonline.util.ErrorManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.jdbc.support.rowset.SqlRowSet;

import java.sql.Types;
import java.util.ArrayList;

@org.springframework.stereotype.Repository(SaleComment.REPOSITORY)
public class SaleCommentRepository implements DataRepository<SaleComment> {

    private static final String TABLE_SALES_COMMENTS = "sales_comments";

    private static final String COLUMNS_SALE_ID = "sale_id";
    private static final String COLUMNS_AUTHOR = "author";
    private static final String COLUMNS_WHOM = "whom";
    private static final String COLUMNS_TEXT = "text";
    private static final String COLUMNS_DATE_TIME = "date_time";

    private static final String COLUMNS_SALES_COMMENTS = "(" +
            COLUMNS_SALE_ID + ", " +
            COLUMNS_AUTHOR + ", " +
            COLUMNS_WHOM + ", " +
            COLUMNS_TEXT + ", " +
            COLUMNS_DATE_TIME + ")";

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

        int result = 1;
        try {
            if (!jdbcOperations.queryForRowSet("SELECT * FROM " + TABLE_SALES_COMMENTS + " WHERE " +
                    COLUMNS_SALE_ID + "=" + object.getSaleId() + " AND " +
                    COLUMNS_AUTHOR + "='" + object.getAuthor() + "' AND " +
                    COLUMNS_DATE_TIME + "=" + object.getDateTime() + ";").next()) {
                result = jdbcOperations.update("INSERT INTO " + TABLE_SALES_COMMENTS + " " + COLUMNS_SALES_COMMENTS + " VALUES (?, ?, ?, ?, ?);", params, types);
            }
        } catch (Exception e) {
            e.printStackTrace();
            ErrorManager.sendError(e.getMessage());
            return 1;
        }
        return result;
    }

    @Override
    public int update(SaleComment object) {
        return 0;
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
        SqlRowSet rowSet = jdbcOperations.queryForRowSet("SELECT * FROM " + TABLE_SALES_COMMENTS + ";");
        while (rowSet.next()) {
            result.add(getSaleComment(rowSet));
        }
        return result;
    }

    public Iterable<SaleComment> getForSale(Sale sale) {
        ArrayList<SaleComment> result = new ArrayList<>();
        SqlRowSet rowSet = jdbcOperations.queryForRowSet("SELECT * FROM " + TABLE_SALES_COMMENTS + " WHERE " + COLUMNS_SALE_ID + "=" + sale.getId() + ";");
        while (rowSet.next()) {
            result.add(getSaleComment(rowSet));
        }
        return result;
    }

    public Iterable<SaleComment> getForSaleId(int saleId) {
        ArrayList<SaleComment> result = new ArrayList<>();
        try {
            SqlRowSet rowSet = jdbcOperations.queryForRowSet("SELECT * FROM " + TABLE_SALES_COMMENTS + " WHERE " + COLUMNS_SALE_ID + "=" + saleId + ";");
            while (rowSet.next()) {
                result.add(getSaleComment(rowSet));
            }
        } catch (Exception e) {
            ErrorManager.sendError(e.getMessage());
            e.printStackTrace();
            return null;
        }
        return result;
    }

    public void removeForSale(int saleId) {
        jdbcOperations.update("DELETE FROM " + TABLE_SALES_COMMENTS + " WHERE " + COLUMNS_SALE_ID + "=" + saleId + ";");
    }

    private SaleComment getSaleComment(SqlRowSet rowSet) {
        return new SaleComment(
                rowSet.getInt(COLUMNS_SALE_ID),
                rowSet.getString(COLUMNS_AUTHOR),
                rowSet.getString(COLUMNS_WHOM),
                rowSet.getString(COLUMNS_TEXT),
                rowSet.getLong(COLUMNS_DATE_TIME));
    }

    public int getCountForSaleId(int saleId) {
        SqlRowSet rowSet = jdbcOperations.queryForRowSet("SELECT COUNT(*) AS count FROM " + TABLE_SALES_COMMENTS + " WHERE " + COLUMNS_SALE_ID + "=" + saleId + ";");
        if (rowSet.next()) {
            return rowSet.getInt("count");
        }
        return 0;
    }
}
