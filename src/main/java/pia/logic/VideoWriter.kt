package pia.logic

import mu.KotlinLogging
import pia.database.Database
import pia.database.model.archive.Video
import pia.exceptions.CreateHashException
import pia.filesystem.FileSystemHelper
import pia.filesystem.MediaType
import pia.logic.tools.VideoInfo
import java.io.IOException

class VideoWriter {
    private val logger = KotlinLogging.logger {  }

    fun addVideo(sourceFilePath : String, videoInfo: VideoInfo, originalFileName : String) : Video? {
        var video : Video? = null
        Database.connection.transactional {
            video = Video.new {
                val fileSystemHelper = FileSystemHelper()
                val file = fileSystemHelper.moveFileToArchive(sourceFilePath, originalFileName, videoInfo.getCreationDate().year, videoInfo.getCreationDate().month, MediaType.Video)
                if (file.exists()) {
                    this.originalFileName = originalFileName
                    pathToFileOnDisk = file.absolutePath
                    creationTime = videoInfo.getCreationDate()
                } else {
                    logger.error("error writing file to disk")
                }
            }
        }
        return video
    }

    fun deleteVideo(video: Video, force : Boolean): Boolean {
        var deleted = false
        Database.connection.transactional {
            val videoFile = video.pathToFileOnDisk
            val fileSystemHelper = FileSystemHelper()
            var deleteFromDb = true
            if (fileSystemHelper.fileExists(videoFile)) {
                if (!fileSystemHelper.deleteFileFromDisk(videoFile)) {
                    deleteFromDb = force
                    logger.error("could not delete videofile from disk $videoFile, delete db-entry: $force")
                }
            } else {
                logger.warn("deleting video {}, file {} does not exist", video.id, video.pathToFileOnDisk)
            }
            if (deleteFromDb) {
                video.delete()
                deleted = true
            }
        }
        return deleted
    }
}