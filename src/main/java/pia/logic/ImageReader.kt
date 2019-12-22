package pia.logic

import pia.database.Database
import pia.database.model.archive.Image
import pia.filesystem.FileSystemHelper
import pia.tools.Logger
import java.io.InputStream
import java.util.*

class ImageReader {
    fun findImageById(imageId: UUID): Optional<Image> {
        return Database.getConnection().query(Image::class.java).findObject(imageId)
    }

    fun findImagesBySHA256(sha256: String): List<Image> {
        return Database.getConnection().query(Image::class.java) {
            Image::sha256Hash.name equal sha256
        }
    }

    fun getImageFileByImageIdFromDisk(imageId: UUID): Optional<InputStream> {
        var fileStream: Optional<InputStream> = Optional.empty()
        val image = findImageById(imageId)
        if (image!!.isPresent) {
            val fileSystemHelper = FileSystemHelper()
            fileStream =
                Optional.of(fileSystemHelper.readFileFromDisk(image.get().pathToFileOnDisk))
        }
        return fileStream
    }

    companion object {
        private val logger = Logger(ImageReader::class.java)
    }
}