package pia.database.model.archive

import jetbrains.exodus.entitystore.Entity
import kotlinx.dnq.XdNaturalEntityType
import pia.database.DbObject
import pia.logic.FileStager
import java.time.LocalDateTime

class StagedFile(entity : Entity) : DbObject(entity) {
    companion object : XdNaturalEntityType<StagedFile>()
    var originalFileName: String = ""
    var pathToFileOnDisk: String = ""
    var creationTime: LocalDateTime? = null
    var stagedType: FileStager.StagedType = FileStager.StagedType.Image
}