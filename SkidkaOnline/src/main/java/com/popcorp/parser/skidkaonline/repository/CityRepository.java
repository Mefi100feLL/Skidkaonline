package com.popcorp.parser.skidkaonline.repository;

import com.popcorp.parser.skidkaonline.entity.City;
import com.popcorp.parser.skidkaonline.util.ErrorManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import rx.Observable;

import java.sql.Types;
import java.util.ArrayList;

@org.springframework.stereotype.Repository(City.REPOSITORY)
public class CityRepository implements DataRepository<City> {

    private static final String TABLE = "cities";

    private static final String COLUMNS_ID = "id";
    private static final String COLUMNS_NAME = "name";
    private static final String COLUMNS_URL = "url";
    private static final String COLUMNS_REGION = "region";

    private static final String[] COLUMNS_CITIES = new String[]{
            COLUMNS_ID,
            COLUMNS_NAME,
            COLUMNS_URL,
            COLUMNS_REGION
    };

    @Autowired
    private JdbcOperations jdbcOperations;


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

        int result = update(object);
        if (result == 0) {
            result = DB.insert(jdbcOperations, TABLE, COLUMNS_CITIES, params, types);
        }

        return result;
    }

    @Override
    public int update(City object) {
        Object[] params = new Object[]{
                object.getId(),
                object.getName(),
                object.getRegion(),
                object.getUrl()
        };

        String[] setColumns = new String[]{
                COLUMNS_ID,
                COLUMNS_NAME,
                COLUMNS_REGION
        };
        String[] selectionColumns = new String[]{COLUMNS_URL};
        return DB.update(jdbcOperations, TABLE, setColumns, selectionColumns, params);
    }

    @Override
    public int remove(City object) {
        return DB.remove(jdbcOperations, TABLE, new String[]{COLUMNS_ID}, new Object[]{object.getId()});
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
            SqlRowSet rowSet = DB.getAll(jdbcOperations, TABLE);
            while (rowSet.next()) {
                result.add(getCity(rowSet));
            }
        } catch (Exception e) {
            ErrorManager.sendError(e.getMessage());
            e.printStackTrace();
            return null;
        }
        return result;
    }

    private City getCity(SqlRowSet rowSet) {
        return new City(
                rowSet.getInt(COLUMNS_ID),
                rowSet.getString(COLUMNS_NAME),
                rowSet.getString(COLUMNS_URL),
                rowSet.getString(COLUMNS_REGION)
        );
    }

    public Observable<City> getObservableAll(){
        return Observable.create(subscriber -> {
            for (City city : getAll()) {
                subscriber.onNext(city);
            }
            subscriber.onCompleted();
        });
    }
}