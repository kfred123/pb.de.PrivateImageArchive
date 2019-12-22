package pia.database

import org.mongodb.morphia.annotations.Id
import java.util.*

open class DbObject {
    @Id
    var id : UUID = UUID.randomUUID()
}