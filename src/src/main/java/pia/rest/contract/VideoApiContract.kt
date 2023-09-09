package pia.rest.contract

import jetbrains.exodus.entitystore.EntityId
import pia.database.model.archive.Video
import pia.logic.VideoReader
import pia.logic.tools.MediaItemInfo
import pia.logic.tools.VideoInfoReader
import pia.rest.CommaSeparatedOptionsParser
import pia.tools.toJavaLocalDateTime
import java.time.LocalDateTime
import java.util.*

class VideoApiContract(val id : EntityId) {
    var fileName : String = ""
    var sha256Hash : String = ""
    var creationTime : LocalDateTime? = null
    var pathToFileNameOnDisk : String = ""
    var videoInfoRawData : MediaItemInfo? = null
    companion object {
        fun fromDbAndEmbedInfos(video : Video, options : CommaSeparatedOptionsParser) : VideoApiContract {
            val apiContract = VideoApiContract(video.entityId)
            apiContract.fileName = video.originalFileName.orEmpty()
            apiContract.sha256Hash = video.sha256Hash.orEmpty()
            apiContract.creationTime = video.creationTime!!.toJavaLocalDateTime()
            apiContract.pathToFileNameOnDisk = video.pathToFileOnDisk.orEmpty()
            if(options.isEnabled("videoMetaInfo")) {
                val info = VideoInfoReader().readVideoInfo(VideoReader().getVideoFile(video))
                apiContract.videoInfoRawData = info
            }
            return apiContract
        }
    }
}
