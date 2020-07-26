package pia.database.model.archive

import org.bson.types.ObjectId
import org.mongodb.morphia.annotations.Entity
import org.mongodb.morphia.annotations.Id
import pia.database.DbObject
import java.time.LocalDateTime
import java.util.*

@Entity("Image")
class Image : DbObject() {
    var sha256Hash : String = ""
    var originalFileName: String = ""
    var pathToFileOnDisk: String = ""
    var creationTime: LocalDateTime? = null
}