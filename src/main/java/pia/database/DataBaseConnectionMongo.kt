package pia.database

import com.mongodb.MongoClient
import org.mongodb.morphia.Datastore
import org.mongodb.morphia.Morphia
import pia.tools.Logger

class DataBaseConnectionMongo(private val databaseName: String) : DataBaseConnection {
    private val mongoClient: MongoClient
    private val morphia: Morphia
    private val datastore: Datastore

    companion object {
        private val logger = Logger(DataBaseConnectionMongo::class.java)
    }

    init {
        mongoClient = MongoClient()
        morphia = Morphia()
        morphia.mapPackage("pia.database.model")
        datastore = morphia.createDatastore(mongoClient, databaseName)
    }

    fun deleteDataBase() {
        mongoClient.dropDatabase(databaseName)
    }

    override fun insertObject(obj: DbObject) {
        datastore.save(obj)
    }

    override fun <T : DbObject> query(clazz: Class<T>, init: DatabaseQuery<T>.() -> Unit) : List<T> {
        val queryMongo = DatabaseQueryMongo<T>(datastore.createQuery(clazz))
        queryMongo.init()
        return queryMongo.getAll()
    }

    override fun <T : DbObject> query(clazz: Class<T>): DatabaseQuery<T> {
        return DatabaseQueryMongo<T>(datastore.createQuery(clazz))
    }

    override fun deleteObject(dbObject: DbObject): Boolean {
        val writeResult = datastore.delete(dbObject)
        if (!writeResult.wasAcknowledged()) {
            val message = String.format(
                "could not delete the object %s with id %s",
                dbObject.javaClass.simpleName,
                dbObject.id
            )
            logger.error(message)
        }
        return writeResult.wasAcknowledged()
    }
}