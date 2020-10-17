package pia.filesystem

import com.drew.imaging.ImageMetadataReader
import com.drew.imaging.ImageProcessingException
import com.drew.metadata.Directory
import com.drew.metadata.Metadata
import com.drew.metadata.exif.ExifIFD0Directory
import com.drew.metadata.icc.IccDirectory
import pia.jobs.GroupDiskFilesByCreationDate
import pia.tools.Logger
import java.io.ByteArrayInputStream
import java.io.IOException
import java.io.InputStream
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.*

class BufferedFileWithMetaData(bytes : ByteArray,
                               val creationDate: LocalDateTime?,
                               val mediaType: MediaType)
    : BufferedFile(bytes) {
    companion object {
        private val logger = Logger(GroupDiskFilesByCreationDate::class.java)
        fun imageFromInputStream(inputStream : InputStream) : BufferedFileWithMetaData {

            val byteData = inputStream.readAllBytes()
            var creationDate : LocalDateTime? = null
            ByteArrayInputStream(byteData).use { tmpInputStream ->
                creationDate = readImageCreationDate(tmpInputStream)?.let {
                    LocalDateTime.ofInstant(it.toInstant(), ZoneId.systemDefault())
                }
            }
            return BufferedFileWithMetaData(byteData, creationDate, MediaType.Image)
        }

        fun videoFromInputStream(inputStream : InputStream) : BufferedFileWithMetaData {

            val byteData = inputStream.readAllBytes()
            var creationDate : LocalDateTime? = null
            ByteArrayInputStream(byteData).use { tmpInputStream ->
                creationDate = readImageCreationDate(tmpInputStream)?.let {
                    LocalDateTime.ofInstant(it.toInstant(), ZoneId.systemDefault())
                }
            }
            return BufferedFileWithMetaData(byteData, creationDate, MediaType.Video)
        }

        private fun readImageCreationDate(inputStream: InputStream): Date? {
            val metadata = readImageMetaData(inputStream)
            var creationDate = metadata?.tryGetTime(IccDirectory::class.java, IccDirectory.TAG_PROFILE_DATETIME)
            if(creationDate == null) {
                creationDate = metadata?.tryGetTime(ExifIFD0Directory::class.java, ExifIFD0Directory.TAG_DATETIME)
            }
            if(creationDate == null) {
                //metadata?.logMetaData()
            }
            return creationDate
        }

        private fun <T : Directory> Metadata.tryGetTime(directoryClass: Class<T>, tag : Int) : Date?{
            val directory = getFirstDirectoryOfType(directoryClass)
            return directory?.getDate(tag)
        }

        private fun readImageMetaData(inputStream: InputStream): Metadata? {
            var metadata: Metadata? = null
            try {
                metadata = ImageMetadataReader.readMetadata(inputStream)
                metadata.logMetaData()
            } catch (e: ImageProcessingException) {
                logger.error("error reading metadata from file %s", e)
            } catch (e: IOException) {
                logger.error("error reading metadata from file %s", e)
            }
            return metadata
        }

        private fun Metadata.logMetaData() {
            directories.forEach {directory ->
                directory.tags.forEach {
                    Logger(BufferedFileWithMetaData.javaClass).info("Directory %s; tagName %s, tagType %s, value %s",
                            directory.javaClass.simpleName, it.tagName, it.tagType, directory.getString(it.tagType))
                }
            }
        }
    }
}