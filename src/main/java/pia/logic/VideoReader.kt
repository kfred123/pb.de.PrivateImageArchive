package pia.logic

import kotlinx.dnq.query.eq
import kotlinx.dnq.query.firstOrNull
import kotlinx.dnq.query.query
import kotlinx.dnq.query.toList
import mu.KotlinLogging
import pia.database.model.archive.Video
import pia.filesystem.FileSystemHelper
import java.io.File
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
            fileStream = Optional.ofNullable(getVideoFileStream(video.get()))
        }
        return fileStream
    }

    fun getVideoFileStream(
        video: Video
    ): InputStream {
        val fileSystemHelper = FileSystemHelper()
        return fileSystemHelper.readFileFromDisk(video.pathToFileOnDisk)
    }

    fun getVideoFile(video: Video) : File {
        return File(video.pathToFileOnDisk)
    }

    companion object {
        private val logger = KotlinLogging.logger {  }
    }
}