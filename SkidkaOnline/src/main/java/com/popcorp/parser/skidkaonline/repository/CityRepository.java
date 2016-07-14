package com.popcorp.parser.skidkaonline.repository;

import com.popcorp.parser.skidkaonline.entity.City;
import com.popcorp.parser.skidkaonline.util.ErrorManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.jdbc.support.rowset.SqlRowSet;

import java.sql.Types;
import java.util.ArrayList;

@org.springframework.stereotype.Repository(City.REPOSITORY)
public class CityRepository implements DataRepository<City> {

    private static final String TABLE_CITIES = "cities";

    private static final String COLUMNS_ID = "id";
    private static final String COLUMNS_NAME = "name";
    private static final String COLUMNS_URL = "url";
    private static final String COLUMNS_REGION = "region";

    private static final String COLUMNS_CITIES = "(" + COLUMNS_ID + ", " + COLUMNS_NAME + ", " + COLUMNS_URL + ", " + COLUMNS_REGION + ")";

    private static final String COLUMNS_CITIES_UPDATE = COLUMNS_ID + "=?, " + COLUMNS_NAME + "=?, " + COLUMNS_REGION + "=?";

    @Autowired
    protected JdbcOperations jdbcOperations;

    @Override
    public int save(City object) {
        Object[] params = new Object[]{
                object.getId(),
                object.getName(),
                object.getUrl(),
                object.getRegion()
        };
        int[] types = new int[]{
                Types.INTEGER,
                Types.VARCHAR,
                Types.VARCHAR,
                Types.VARCHAR
        };

        int countOfUpdated = update(object);
        if (countOfUpdated == 0) {
            return jdbcOperations.update("INSERT INTO " + TABLE_CITIES + " " + COLUMNS_CITIES + " VALUES (?, ?, ?, ?);", params, types);
        } else {
            return countOfUpdated;
        }
    }

    @Override
    public int update(City object) {
        Object[] params = new Object[]{
                object.getId(),
                object.getName(),
                object.getRegion(),
                object.getUrl()
        };

        return jdbcOperations.update("UPDATE " + TABLE_CITIES + " SET " + COLUMNS_CITIES_UPDATE + " WHERE " + COLUMNS_URL + "=?;", params);
    }

    @Override
    public int save(Iterable<City> objects) {
        int count = 0;
        for (City city : objects) {
            count += save(city);
        }
        return count;
    }

    @Override
    public Iterable<City> getAll() {
        ArrayList<City> result = new ArrayList<>();
        try {
            SqlRowSet rowSet = jdbcOperations.queryForRowSet("SELECT * FROM " + TABLE_CITIES + ";");
            while (rowSet.next()) {
                City city = new City(
                        rowSet.getInt(COLUMNS_ID),
                        rowSet.getString(COLUMNS_NAME),
                        rowSet.getString(COLUMNS_URL),
                        rowSet.getString(COLUMNS_REGION)
                );
                result.add(city);
            }
        } catch (Exception e){
            ErrorManager.sendError(e.getMessage());
            e.printStackTrace();
            return null;
        }
        return result;
    }
}