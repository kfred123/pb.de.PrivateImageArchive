package pia.database

import java.util.*

interface DatabaseQuery<T : DbObject> {
    fun findObject(id: UUID): Optional<T>
    fun getAll(): List<T>

    infix fun String.equal(objVal : Any) : DatabaseQuery<T>
}