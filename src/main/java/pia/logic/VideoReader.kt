package pia.logic

import pia.database.Database
import pia.database.model.archive.Video
import pia.filesystem.FileSystemHelper
import pia.tools.Logger
import java.io.InputStream
import java.util.*

class VideoReader {
    fun findVideoById(videoId: UUID): Optional<Video> {
        return Database.getConnection().query(Video::class.java).findObject(videoId)
    }

    fun findVideosBySHA256(sha256: String): List<Video> {
        return Database.getConnection().query(Video::class.java) {
            Video::sha256Hash.name equal sha256
        }
    }

    fun getVideoFileByVideoIdFromDisk(videoId: UUID): Optional<InputStream> {
        var fileStream: Optional<InputStream> = Optional.empty()
        val video = findVideoById(videoId)
        if (video!!.isPresent) {
            val fileSystemHelper = FileSystemHelper()
            fileStream =
                Optional.of(fileSystemHelper.readFileFromDisk(video.get().pathToFileOnDisk))
        }
        return fileStream
    }

    companion object {
        private val logger = Logger(VideoReader::class.java)
    }
}