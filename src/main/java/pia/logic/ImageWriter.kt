package pia.logic

import mu.KotlinLogging
import pia.database.Database
import pia.database.model.archive.Image
import pia.exceptions.CreateHashException
import pia.filesystem.BufferedFileWithMetaData
import pia.filesystem.FileSystemHelper
import java.io.IOException
import kotlin.jvm.internal.Intrinsics.Kotlin

class ImageWriter {
    private val logger = KotlinLogging.logger {  }
    @Throws(IOException::class, CreateHashException::class)
    fun addImage(bufferedFile: BufferedFileWithMetaData, fileName: String?) {
        Database.connection.transactional {
            val image = Image.new {
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

    fun deleteImage(image: Image): Boolean {
        var deleted = false
        Database.connection.transactional {
            val imageFile = image.pathToFileOnDisk
            val fileSystemHelper = FileSystemHelper()
            var deleteFromDb = true
            if (fileSystemHelper.fileExists(imageFile)) {
                if (!fileSystemHelper.deleteFileFromDisk(imageFile)) {
                    deleteFromDb = false
                    logger.error("could not imagefile from disk {}, db-entry will not be deleted", imageFile)
                }
            } else {
                logger.warn("deleting image ${image.id}, file ${image.pathToFileOnDisk} does not exist")
                deleteFromDb = false
            }
            if (deleteFromDb) {
                image.delete()
                deleted = true
            }
        }
        return deleted
    }
}