package pia.logic

import mu.KotlinLogging
import org.joda.time.DateTime
import pia.database.Database
import pia.database.model.archive.Image
import pia.exceptions.CreateHashException
import pia.filesystem.FileSystemHelper
import pia.filesystem.MediaType
import pia.logic.tools.ImageInfo
import pia.tools.toJodaDateTime
import java.io.IOException
import java.time.ZoneId
import java.util.*

class ImageWriter {
    private val logger = KotlinLogging.logger {  }

    fun addImage(sourceFilePath : String, imageInfo: ImageInfo, originalFileName : String) : Image? {
        var image : Image? = null
        image = Image.new {
            val fileSystemHelper = FileSystemHelper()
            val year = imageInfo.getCreationDate().year
            val month = imageInfo.getCreationDate().month
            val file =
                fileSystemHelper.moveFileToArchive(sourceFilePath, originalFileName, year, month, MediaType.Image)
            if (file.exists()) {
                this.originalFileName = originalFileName
                pathToFileOnDisk = file.absolutePath
                creationTime = imageInfo.getCreationDate().toJodaDateTime()
            } else {
                logger.error("error writing file to disk")
            }
        }
        return image
    }

    fun deleteImage(image: Image, force : Boolean): Boolean {
        var deleted = false
        Database.connection.transactional {
            val imageFile = image.pathToFileOnDisk!!
            val fileSystemHelper = FileSystemHelper()
            var deleteFromDb = true
            if (fileSystemHelper.fileExists(imageFile)) {
                if (!fileSystemHelper.deleteFileFromDisk(imageFile)) {
                    deleteFromDb = force
                    logger.error("could not delete imagefile from disk $imageFile, delete db-entry: $force")
                }
            } else {
                logger.warn("deleting image ${image.entityId}, file ${image.pathToFileOnDisk} does not exist, delete db-entry: $force")
            }
            if (deleteFromDb) {
                image.delete()
                deleted = true
            }
            it.commit()
        }
        return deleted
    }
}