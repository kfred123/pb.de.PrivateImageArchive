package pia.database.model.archive

import jetbrains.exodus.entitystore.Entity
import kotlinx.dnq.XdNaturalEntityType
import kotlinx.dnq.xdDateTimeProp
import kotlinx.dnq.xdLink0_1
import kotlinx.dnq.xdStringProp
import pia.database.DbObject

class StagedFile(entity : Entity) : DbObject(entity) {
    companion object : XdNaturalEntityType<StagedFile>()
    var originalFileName by xdStringProp {  }
    var pathToFileOnDisk by xdStringProp {  }
    var creationTime by xdDateTimeProp {  }
    var stagedTypeEntity by xdLink0_1(StagedTypeEntity)
}