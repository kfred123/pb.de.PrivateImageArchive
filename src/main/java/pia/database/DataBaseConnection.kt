package pia.database

interface DataBaseConnection {
    fun insertObject(obj: DbObject)
    fun <T : DbObject> query(clazz: Class<T>, init: DatabaseQuery<T>.() -> Unit = {}) : List<T>
    fun <T : DbObject> query(clazz: Class<T>) : DatabaseQuery<T>

    fun deleteObject(dbObject: DbObject): Boolean
}