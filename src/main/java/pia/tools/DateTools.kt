package pia.tools

import org.joda.time.DateTime
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.Calendar
import java.util.Date

class DateTools {
    companion object {
        fun toLocalDateTime(date : Date) : LocalDateTime {
            return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime()
        }
    }
}

fun LocalDateTime.toJodaDateTime() : DateTime {
    return DateTime(atZone(ZoneId.systemDefault()).toInstant().toEpochMilli())
}

fun org.joda.time.DateTime.toJavaLocalDateTime() : LocalDateTime {
    return LocalDateTime.ofInstant(Instant.ofEpochMilli(this.millis), ZoneId.systemDefault())
}