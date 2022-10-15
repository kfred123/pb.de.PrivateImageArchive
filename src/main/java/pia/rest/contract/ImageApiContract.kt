package pia.rest.contract

import pia.database.model.archive.Image
import java.time.LocalDateTime
import java.util.*

class ImageApiContract(val id : UUID) {
    var fileName : String = ""
    var sha256Hash : String = ""
    var creationTime : LocalDateTime? = null
    var pathToFileNameOnDisk : String = ""
    companion object {
        fun fromDb(image : Image) : ImageApiContract {
            val apiContract = ImageApiContract(image.id)
            apiContract.fileName = image.originalFileName
            apiContract.sha256Hash = image.sha256Hash
            apiContract.creationTime = image.creationTime
            apiContract.pathToFileNameOnDisk = image.pathToFileOnDisk
            return apiContract
        }
    }
}
