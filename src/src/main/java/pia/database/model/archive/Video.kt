package pia.database.model.archive

import jetbrains.exodus.entitystore.Entity
import kotlinx.dnq.XdNaturalEntityType
import kotlinx.dnq.xdDateTimeProp
import kotlinx.dnq.xdStringProp
import pia.database.DbObject
import pia.logic.tools.MediaItemInfo
import java.time.LocalDateTime

class Video(entity: Entity) : DbObject(entity) {
    companion object : XdNaturalEntityType<Video>()
    var sha256Hash by xdStringProp {  }
    var originalFileName by xdStringProp {  }
    var pathToFileOnDisk by xdStringProp {  }
    var creationTime by xdDateTimeProp {  }
}