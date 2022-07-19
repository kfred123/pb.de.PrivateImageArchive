package pia.rest.contract

import pia.database.model.archive.Video
import java.time.LocalDateTime
import java.util.*

class VideoApiContract(val id : UUID) {
    var fileName : String = ""
    var sha256Hash : String = ""
    var creationTime : LocalDateTime? = null

    companion object {
        fun fromDb(video : Video) : VideoApiContract {
            val apiContract = VideoApiContract(video.id)
            apiContract.fileName = video.originalFileName
            apiContract.sha256Hash = video.sha256Hash
            apiContract.creationTime = video.creationTime
            return apiContract
        }
    }
}
