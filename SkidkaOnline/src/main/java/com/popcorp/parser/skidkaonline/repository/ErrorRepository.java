package com.popcorp.parser.skidkaonline.repository;

import com.popcorp.parser.skidkaonline.entity.Error;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.jdbc.support.rowset.SqlRowSet;

import java.sql.Types;
import java.util.ArrayList;

@org.springframework.stereotype.Repository(Error.REPOSITORY)
public class ErrorRepository implements DataRepository<Error> {

    private static final String TABLE = "errors";

    private static final String COLUMN_BODY = "body";
    private static final String COLUMN_SUBJECT = "subject";
    private static final String COLUMN_DATETIME = "datetime";

    private static final String[] COLUMNS = new String[]{
            COLUMN_BODY,
            COLUMN_SUBJECT,
            COLUMN_DATETIME
    };

    @Autowired
    protected JdbcOperations jdbcOperations;

    @Override
    public int save(Error object) {
        Object[] params = new Object[]{
                object.getBody(),
                object.getSubject(),
                object.getDateTime()
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

        return result;
    }

    @Override
    public int update(Error object) {
        Object[] params = new Object[]{
                object.getBody(),
                object.getSubject(),
                object.getDateTime(),
                object.getBody(),
                object.getSubject(),
                object.getDateTime()
        };

        String[] setColumns = new String[]{
                COLUMN_BODY,
                COLUMN_SUBJECT,
                COLUMN_DATETIME
        };
        String[] selectionColumns = new String[]{
                COLUMN_BODY,
                COLUMN_SUBJECT,
                COLUMN_DATETIME
        };

        return DB.update(jdbcOperations, TABLE, setColumns, selectionColumns, params);
    }

    @Override
    public int remove(Error object) {
        String[] selectionColumns = new String[]{
                COLUMN_BODY,
                COLUMN_SUBJECT,
                COLUMN_DATETIME
        };
        Object[] selectionValues = new Object[]{
                object.getBody(),
                object.getSubject(),
                object.getDateTime()
        };
        return DB.remove(jdbcOperations, TABLE, selectionColumns, selectionValues);
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
        SqlRowSet rowSet = DB.getAll(jdbcOperations, TABLE);
        if (rowSet != null) {
            while (rowSet.next()) {
                result.add(getError(rowSet));
            }
        }
        return result;
    }

    private Error getError(SqlRowSet rowSet) {
        return new Error(
                rowSet.getString(COLUMN_SUBJECT),
                rowSet.getString(COLUMN_BODY),
                rowSet.getString(COLUMN_DATETIME)
        );
    }

}
