package pia.database;

import pia.database.model.archive.Image;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.UUID;

public class DataBaseConnectionMongoTest {
    DataBaseConnectionMongo dataBaseConnectionMongo;
    @Before
    public void prepare() {
        dataBaseConnectionMongo = new DataBaseConnectionMongo(UUID.randomUUID().toString());
    }

    @After
    public void cleanUp() {
        dataBaseConnectionMongo.deleteDataBase();
    }

    @Test
    public void testInsertImage() {
        dataBaseConnectionMongo.insertObject(new Image());
    }

}
