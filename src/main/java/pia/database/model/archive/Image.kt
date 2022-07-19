package pia.database.model.archive

import jetbrains.exodus.entitystore.Entity
import kotlinx.dnq.XdNaturalEntityType
import pia.database.DbObject
import java.time.LocalDateTime

class Image(entity: Entity) : DbObject(entity) {
    companion object : XdNaturalEntityType<Image>()

    var sha256Hash : String = ""
    var originalFileName: String = ""
    var pathToFileOnDisk: String = ""
    var creationTime: LocalDateTime? = null
}