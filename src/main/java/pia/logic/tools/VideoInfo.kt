package pia.logic.tools

import org.json.JSONWriter
import org.mp4parser.boxes.iso14496.part12.MovieBox
import pia.tools.DateTools
import java.time.LocalDateTime

class VideoInfo(movieBox: MovieBox) : MediaItemInfo {
    private val creationDate : LocalDateTime
    val rawDataInfo : String

    init {
        creationDate = DateTools.toLocalDateTime(movieBox.movieHeaderBox.creationTime)
        rawDataInfo = JSONWriter.valueToString(movieBox.movieHeaderBox)
    }

    override fun getCreationDate(): LocalDateTime {
        return creationDate
    }
}