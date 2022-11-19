package pia.tools

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