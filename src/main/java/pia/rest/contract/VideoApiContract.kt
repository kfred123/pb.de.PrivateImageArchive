package pia.rest.contract

import pia.database.model.archive.Video
import pia.logic.VideoReader
import pia.logic.tools.MediaItemInfo
import pia.logic.tools.VideoInfoReader
import pia.rest.CommaSeparatedOptionsParser
import java.time.LocalDateTime
import java.util.*

class VideoApiContract(val id : UUID) {
    var fileName : String = ""
    var sha256Hash : String = ""
    var creationTime : LocalDateTime? = null
    var pathToFileNameOnDisk : String = ""
    var videoInfoRawData : MediaItemInfo? = null
    companion object {
        fun fromDbAndEmbedInfos(video : Video, options : CommaSeparatedOptionsParser) : VideoApiContract {
            val apiContract = VideoApiContract(video.id)
            apiContract.fileName = video.originalFileName
            apiContract.sha256Hash = video.sha256Hash
            apiContract.creationTime = video.creationTime
            apiContract.pathToFileNameOnDisk = video.pathToFileOnDisk
            if(options.isEnabled("videoMetaInfo")) {
                val info = VideoInfoReader().readVideoInfo(VideoReader().getVideoFileStream(video))
                apiContract.videoInfoRawData = info
            }
            return apiContract
        }
    }
}
