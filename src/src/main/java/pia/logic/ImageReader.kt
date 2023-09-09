package pia.logic

import jetbrains.exodus.entitystore.EntityId
import kotlinx.dnq.query.*
import mu.KotlinLogging
import pia.database.Database
import pia.database.model.archive.Image
import pia.filesystem.FileSystemHelper
import java.io.InputStream
import java.util.*

class ImageReader {
    fun findImageById(imageId: EntityId): Optional<Image> {
        var result = Optional.empty<Image>()
        Database.connection.transactional(true) {
            result = Optional.ofNullable(Image.query(Image::entityId eq imageId).firstOrNull())
        }
        return result
    }

    fun findImagesBySHA256(sha256: String): List<Image> {
        return Image.query(Image::sha256Hash eq sha256).toList()
    }

    fun getImageFileByImageIdFromDisk(imageId: EntityId): Optional<InputStream> {
        var fileStream: Optional<InputStream> = Optional.empty()
        val image = findImageById(imageId)
        if (image.isPresent) {
            val fileSystemHelper = FileSystemHelper()
            fileStream =
                Optional.of(fileSystemHelper.readFileFromDisk(image.get().pathToFileOnDisk!!))
        }
        return fileStream
    }

    companion object {
        private val logger = KotlinLogging.logger {  }
    }
}