package com.popcorp.parser.skidkaonline.repository;

import com.popcorp.parser.skidkaonline.entity.DomainObject;

public interface DataRepository<V extends DomainObject> {

    int save(V object);

    int update(V object);

    int save(Iterable<V> objects);

    Iterable<V> getAll();

}