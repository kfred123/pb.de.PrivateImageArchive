package pia.logic.tools

import org.json.JSONWriter
import org.mp4parser.boxes.iso14496.part12.MovieBox
import org.reflections.serializers.JsonSerializer
import java.time.LocalDateTime
import java.util.Date

class ImageInfo(creationDate : LocalDateTime) : MediaItemInfo {
    private val creationDate : LocalDateTime
    //val rawDataInfo : String

    init {
        this.creationDate = creationDate
        //creationDate = movieBox.movieHeaderBox.creationTime
        //rawDataInfo = JSONWriter.valueToString(movieBox.movieHeaderBox)
    }

    override fun getCreationDate(): LocalDateTime {
        return creationDate
    }
}