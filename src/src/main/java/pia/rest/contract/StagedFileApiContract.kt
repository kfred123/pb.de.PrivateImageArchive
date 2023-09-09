package pia.rest.contract

import jetbrains.exodus.entitystore.EntityId
import pia.database.model.archive.Image
import pia.database.model.archive.StagedFile
import pia.tools.toJavaLocalDateTime
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.*

class StagedFileApiContract(val id : EntityId) {
    var fileName : String = ""
    var creationTime : LocalDateTime? = null
    var pathToFileNameOnDisk : String = ""
    companion object {
        fun fromDb(image : StagedFile) : StagedFileApiContract {
            val apiContract = StagedFileApiContract(image.entityId)
            apiContract.fileName = image.originalFileName.orEmpty()
            apiContract.creationTime = image.creationTime!!.toJavaLocalDateTime()
            apiContract.pathToFileNameOnDisk = image.pathToFileOnDisk.orEmpty()
            return apiContract
        }
    }
}
