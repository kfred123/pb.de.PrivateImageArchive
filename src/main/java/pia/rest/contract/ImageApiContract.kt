package pia.rest.contract

import jetbrains.exodus.entitystore.EntityId
import pia.database.model.archive.Image
import java.time.LocalDateTime
import java.util.*

class ImageApiContract(val id : EntityId) {
    var fileName : String = ""
    var sha256Hash : String = ""
    var creationTime : org.joda.time.LocalDateTime? = null
    var pathToFileNameOnDisk : String = ""
    companion object {
        fun fromDb(image : Image) : ImageApiContract {
            val apiContract = ImageApiContract(image.entityId)
            apiContract.fileName = image.originalFileName.orEmpty()
            apiContract.sha256Hash = image.sha256Hash.orEmpty()
            apiContract.creationTime = image.creationTime!!.toLocalDateTime()
            apiContract.pathToFileNameOnDisk = image.pathToFileOnDisk.orEmpty()
            return apiContract
        }
    }
}
