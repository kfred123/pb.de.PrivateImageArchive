package pia.rest.contract

import pia.database.model.archive.Image
import java.util.*
import javax.xml.bind.annotation.XmlRootElement

@XmlRootElement
class ImageApiContract(val id : UUID) {
    var fileName : String = ""
    var sha256Hash : String = ""

    companion object {
        fun fromDb(image : Image) : ImageApiContract {
            val apiContract = ImageApiContract(image.id)
            apiContract.fileName = image.originalFileName
            apiContract.sha256Hash = image.sha256Hash
            return apiContract
        }
    }
}
