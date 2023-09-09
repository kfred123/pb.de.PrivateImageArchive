package pia.tools

import jetbrains.exodus.entitystore.EntityId
import jetbrains.exodus.entitystore.PersistentEntityId

class EntityIdConverter {
}

fun String.toEntityId() : EntityId {
    val ids = split("-")
    return PersistentEntityId(ids[0].toInt(), ids[1].toLong())
}