package com.popcorp.parser.skidkaonline.repository;

import com.popcorp.parser.skidkaonline.entity.Error;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.jdbc.support.rowset.SqlRowSet;

import java.sql.Types;
import java.util.ArrayList;

@org.springframework.stereotype.Repository(Error.REPOSITORY)
public class ErrorRepository implements DataRepository<Error> {

    private static final String TABLE_ERRORS = "errors";

    private static final String COLUMNS_BODY = "body";
    private static final String COLUMNS_SUBJECT = "subject";

    private static final String COLUMNS_ERRORS = "(" + COLUMNS_BODY + ", " + COLUMNS_SUBJECT + ")";

    @Autowired
    protected JdbcOperations jdbcOperations;

    @Override
    public int save(Error object) {
        Object[] params = new Object[]{object.getBody(), object.getSubject()};
        int[] types = new int[]{Types.VARCHAR, Types.VARCHAR};

        int result = 1;
        try {
            result = jdbcOperations.update("INSERT INTO " + TABLE_ERRORS + " " + COLUMNS_ERRORS + " VALUES (?, ?);", params, types);
        } catch (Exception e) {
            result = 1;
        }
        return result;
    }

    @Override
    public int update(Error object) {
        return 0;
    }

    public boolean exist(Error object) {
        return jdbcOperations.queryForRowSet("SELECT * FROM " + TABLE_ERRORS + " WHERE " + COLUMNS_BODY + "='" + object.getBody() + "' AND " + COLUMNS_SUBJECT + "='" + object.getSubject() + "';").next();
    }

    @Override
    public int save(Iterable<Error> objects) {
        int count = 0;
        for (Error error : objects) {
            count += save(error);
        }
        return count;
    }

    @Override
    public Iterable<Error> getAll() {
        ArrayList<Error> result = new ArrayList<>();
        SqlRowSet rowSet = jdbcOperations.queryForRowSet("SELECT * FROM " + TABLE_ERRORS + ";");
        while (rowSet.next()) {
            Error city = new Error(rowSet.getString(COLUMNS_SUBJECT), rowSet.getString(COLUMNS_BODY));
            result.add(city);
        }
        return result;
    }

    public int remove(Error error) {
        return jdbcOperations.update("DELETE FROM " + TABLE_ERRORS + " WHERE " + COLUMNS_SUBJECT + "='" + error.getSubject() + "' AND " + COLUMNS_BODY + "='" + error.getBody() + "';");
    }
}
