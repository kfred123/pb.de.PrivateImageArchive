package pia.database.model.archive

import org.mongodb.morphia.annotations.Entity
import pia.database.DbObject
import java.time.LocalDateTime

@Entity("Video")
class Video : DbObject() {
    var sha256Hash : String = ""
    var originalFileName: String = ""
    var pathToFileOnDisk: String = ""
    var creationTime: LocalDateTime? = null
}