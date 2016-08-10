package com.popcorp.parser.skidkaonline.repository;

import com.popcorp.parser.skidkaonline.util.ErrorManager;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.jdbc.support.rowset.SqlRowSet;

import java.util.Arrays;

public class DB {

    public static int getCount(JdbcOperations jdbcOperations, String table, String[] selectionColumns, Object[] selectionValues) {
        String selection = getSelection(selectionColumns, selectionValues);
        if (selection != null) {
            SqlRowSet rowSet = jdbcOperations.queryForRowSet("SELECT COUNT(*) AS count FROM " + table + " WHERE " + selection + ";");
            if (rowSet != null && rowSet.next()) {
                return rowSet.getInt("count");
            }
        }
        return 0;
    }

    private static String getSelection(String[] selectionColumns, Object[] selectionValues) {
        String result = null;
        if (selectionColumns.length != selectionValues.length) {
            ErrorManager.sendError("COLUMNS AND VALUES LENGHT NOT EQUALS columns=" + Arrays.toString(selectionColumns) + ", values=" + Arrays.toString(selectionValues));
        } else {
            result = "";
            for (int i = 0; i < selectionColumns.length; i++) {
                result += selectionColumns[i] + "=";
                if (selectionValues[i] instanceof String) {
                    result += "'" + selectionValues[i] + "'";
                } else {
                    result += selectionValues[i];
                }
                if (i != selectionColumns.length - 1) {
                    result += " AND ";
                }
            }
        }
        return result;
    }

    public static int update(JdbcOperations jdbcOperations, String table, String[] setColumns, String[] selectionColumns, Object[] params) {
        String set = "";
        for (String column : setColumns) {
            set += column + "=?";
            if (!setColumns[setColumns.length - 1].equals(column)) {
                set += ", ";
            }
        }
        String selection = "";
        for (String column : selectionColumns) {
            selection += column + "=?";
            if (!selectionColumns[selectionColumns.length - 1].equals(column)) {
                selection += " AND ";
            }
        }
        return jdbcOperations.update("UPDATE " + table + " SET " + set + " WHERE " + selection + ";", params);
    }

    public static int insert(JdbcOperations jdbcOperations, String table, String[] columns, Object[] params, int[] types) {
        try {
            String columnsStr = Arrays.toString(columns).replace("[", "(").replace("]", ")");
            String valuesStr = Arrays.toString(types).replaceAll("(-)*[0-9]+", "?").replace("[", "(").replace("]", ")");
            return jdbcOperations.update("INSERT INTO " + table + " " + columnsStr + " VALUES " + valuesStr + ";", params, types);
        } catch (Exception e) {
            return 1;
        }
    }

    public static int remove(JdbcOperations jdbcOperations, String table, String[] selectionColumns, Object[] selectionValues) {
        String selection = getSelection(selectionColumns, selectionValues);
        if (selection != null) {
            return jdbcOperations.update("DELETE FROM " + table + " WHERE " + selection +";");
        }
        return 0;
    }

    public static SqlRowSet getAll(JdbcOperations jdbcOperations, String table) {
        return jdbcOperations.queryForRowSet("SELECT * FROM " + table + ";");
    }

    public static SqlRowSet getAllWithInnerJoin(JdbcOperations jdbcOperations, String tableFirst, String tableSecond, String columnFirst, String columnSecond) {
        return jdbcOperations.queryForRowSet("SELECT * FROM " + tableFirst + " INNER JOIN " + tableSecond + " ON " + columnFirst + "=" + columnSecond + ";");
    }

    public static SqlRowSet get(JdbcOperations jdbcOperations, String table, String[] selectionColumns, Object[] selectionValues) {
        String selection = getSelection(selectionColumns, selectionValues);
        if (selection != null) {
            return jdbcOperations.queryForRowSet("SELECT * FROM " + table + " WHERE " + selection + ";");
        }
        return null;
    }

    public static SqlRowSet getWithInnerJoin(JdbcOperations jdbcOperations, String tableFirst, String tableSecond, String columnFirst, String columnSecond, String[] selectionColumns, Object[] selectionValues) {
        String selection = getSelection(selectionColumns, selectionValues);
        if (selection != null) {
            return jdbcOperations.queryForRowSet("SELECT * FROM " + tableFirst + " INNER JOIN " + tableSecond + " ON " + columnFirst + "=" + columnSecond + " WHERE " + selection + ";");
        }
        return null;
    }
}
