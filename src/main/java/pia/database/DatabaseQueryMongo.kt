package pia.database

import org.mongodb.morphia.query.Query
import pia.tools.Logger
import java.util.*

class DatabaseQueryMongo<T : DbObject>(private var mongoQuery: Query<T>) : DatabaseQuery<T> {
    companion object {
        private val logger = Logger(DatabaseQueryMongo::class.java)
    }

    override fun findObject(id: UUID): Optional<T> {
        var result: Optional<T> = Optional.empty()
        val resultList = mongoQuery.field("id").equal(id).asList()
        if (resultList.size > 0) {
            result = Optional.ofNullable(resultList[0])
            if (resultList.size > 1) {
                logger.error(
                    String.format(
                        "found more than one Object for id %s and type %s",
                        id,
                        mongoQuery.entityClass
                    )
                )
            }
        }
        return result
    }

    override fun getAll(): List<T> {
        return mongoQuery.asList()
    }

    override fun String.equal(objVal : Any) : DatabaseQuery<T> {
        mongoQuery = mongoQuery.field(this).equal(objVal)
        return this@DatabaseQueryMongo
    }



}