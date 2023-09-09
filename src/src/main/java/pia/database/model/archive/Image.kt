package pia.database.model.archive

import jetbrains.exodus.entitystore.Entity
import kotlinx.dnq.XdNaturalEntityType
import kotlinx.dnq.xdDateTimeProp
import kotlinx.dnq.xdStringProp
import pia.database.DbObject
import java.time.LocalDateTime

class Image(entity: Entity) : DbObject(entity) {
    companion object : XdNaturalEntityType<Image>()

    var sha256Hash by xdStringProp {  }
    var originalFileName by xdStringProp {}
    var pathToFileOnDisk by xdStringProp {}
    var creationTime by xdDateTimeProp {  }
}