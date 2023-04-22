package pia.database

import jetbrains.exodus.entitystore.Entity
import kotlinx.dnq.XdEntity
import kotlinx.dnq.XdNaturalEntityType
import java.util.*

open class DbObject(entity : Entity) : XdEntity(entity) {
    companion object : XdNaturalEntityType<DbObject>()
}