package pia.database;

import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.annotations.Entity;
import com.mongodb.MongoClient;
import org.mongodb.morphia.Morphia;
import org.mongodb.morphia.query.Query;

import java.util.Optional;

public class DataBaseConnectionMongo implements DataBaseConnection {
    private MongoClient mongoClient;
    private Morphia morphia;
    private final Datastore datastore;
    private final String databaseName;

    public DataBaseConnectionMongo(String databaseName) {
        this.databaseName = databaseName;
        mongoClient = new MongoClient();
        morphia = new Morphia();
        morphia.mapPackage("pia.database.model");
        datastore = morphia.createDatastore(mongoClient, databaseName);
    }

    public void deleteDataBase() {
         mongoClient.dropDatabase(databaseName);
    }

    @Override
    public void insertObject(DbObject object) {
        datastore.save(object);
    }

    @Override
    public Query createQuery(Class clazz) {
        return datastore.createQuery(clazz);
    }
}
