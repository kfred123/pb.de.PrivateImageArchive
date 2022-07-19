package pia.logic

import kotlinx.dnq.query.eq
import kotlinx.dnq.query.firstOrNull
import kotlinx.dnq.query.query
import kotlinx.dnq.query.toList
import mu.KotlinLogging
import pia.database.model.archive.Video
import pia.filesystem.FileSystemHelper
import java.io.InputStream
import java.util.*
import kotlin.jvm.internal.Intrinsics.Kotlin


class VideoReader {
    fun findVideoById(videoId: UUID): Optional<Video> {
        return Optional.ofNullable(Video.query(Video::id eq videoId).firstOrNull())
    }

    fun findVideosBySHA256(sha256: String): List<Video> {
        return Video.query(Video::sha256Hash eq sha256).toList()
    }

    fun getVideoFileByVideoIdFromDisk(videoId: UUID): Optional<InputStream> {
        var fileStream: Optional<InputStream> = Optional.empty()
        val video = findVideoById(videoId)
        if (video.isPresent) {
            val fileSystemHelper = FileSystemHelper()
            fileStream =
                Optional.of(fileSystemHelper.readFileFromDisk(video.get().pathToFileOnDisk))
        }
        return fileStream
    }

    companion object {
        private val logger = KotlinLogging.logger {  }
    }
}