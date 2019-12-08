package pia.database;

import org.mongodb.morphia.query.Query;

import java.util.Optional;

public interface DataBaseConnection {
    void insertObject(DbObject object);

    Query createQuery(Class clazz);
}
