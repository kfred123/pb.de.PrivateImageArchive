package pia.logic

import mu.KotlinLogging
import pia.database.Database
import pia.database.model.archive.Video
import pia.exceptions.CreateHashException
import pia.filesystem.BufferedFileWithMetaData
import pia.filesystem.FileSystemHelper
import java.io.IOException

class VideoWriter {
    private val logger = KotlinLogging.logger {  }
    @Throws(IOException::class, CreateHashException::class)
    fun addVideo(bufferedFile: BufferedFileWithMetaData, fileName: String?) {
        Database.connection.transactional {
            val video = Video.new {
                val fileSystemHelper = FileSystemHelper()
                val fileOnDisk = id.toString() + "." + fileSystemHelper.getFileExtension(fileName!!)
                val file = fileSystemHelper.writeFileToDisk(bufferedFile, fileOnDisk)
                if (file.exists()) {
                    originalFileName = fileName
                    pathToFileOnDisk = file.absolutePath
                    creationTime = bufferedFile.mediaItemInfo.getCreationDate()
                } else {
                    logger.error("error writing file to disk")
                }
            }
        }
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