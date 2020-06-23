package pia.filesystem

import com.drew.imaging.ImageMetadataReader
import com.drew.imaging.ImageProcessingException
import com.drew.metadata.Metadata
import com.drew.metadata.exif.ExifSubIFDDirectory
import com.drew.metadata.icc.IccDirectory
import com.drew.metadata.png.PngDirectory
import pia.filesystem.BufferedFileWithMetaData.Companion.readImageCreationDate
import pia.jobs.GroupDiskFilesByCreationDate
import pia.tools.Logger
import java.io.*
import java.time.*
import java.util.*
import java.util.function.Consumer

class BufferedFileWithMetaData(bytes : ByteArray,
                               val creationDate: LocalDateTime?)
    : BufferedFile(bytes) {
    companion object {
        private val logger = Logger(GroupDiskFilesByCreationDate::class.java)
        fun fromInputStream(inputStream : InputStream) : BufferedFileWithMetaData {

            val byteData = inputStream.readAllBytes()
            var creationDate : LocalDateTime? = null
            ByteArrayInputStream(byteData).use { tmpInputStream ->
                creationDate = readImageCreationDate(tmpInputStream)?.let {
                    LocalDateTime.ofInstant(it.toInstant(), ZoneId.systemDefault())
                }
            }
            return BufferedFileWithMetaData(byteData, creationDate)
        }


        private fun readImageCreationDate(inputStream: InputStream): Date? {
            val metadata = readImageMetaData(inputStream)
            val directory = metadata?.getFirstDirectoryOfType(IccDirectory::class.java)
            IccDirectory.TAG_PROFILE_DATETIME
            PngDirectory.TAG_LAST_MODIFICATION_TIME
            return directory?.getDate(IccDirectory.TAG_PROFILE_DATETIME)
        }

        private fun readImageMetaData(inputStream: InputStream): Metadata? {
            var metadata: Metadata? = null
            try {
                metadata = ImageMetadataReader.readMetadata(inputStream)
                metadata.directories.forEach {directory ->
                    directory.tags.forEach {
                        Logger(BufferedFileWithMetaData.javaClass).info("Directory {}; tagName {}, tagType {}, value {}",
                        directory.javaClass, it.tagName, it.tagType, directory.getString(it.tagType))
                    }
                }
            } catch (e: ImageProcessingException) {
                logger.error("error reading metadata from file %s", e)
            } catch (e: IOException) {
                logger.error("error reading metadata from file %s", e)
            }
            return metadata
        }
    }
}