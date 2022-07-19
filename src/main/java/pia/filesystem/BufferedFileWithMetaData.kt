package pia.filesystem

import com.drew.imaging.ImageMetadataReader
import com.drew.metadata.Directory
import com.drew.metadata.Metadata
import com.drew.metadata.exif.ExifIFD0Directory
import com.drew.metadata.icc.IccDirectory
import com.drew.metadata.mp4.Mp4Directory
import mu.KotlinLogging
import pia.jobs.GroupDiskFilesByCreationDate
import java.io.ByteArrayInputStream
import java.io.InputStream
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.*

class BufferedFileWithMetaData(bytes : ByteArray,
                               val creationDate: LocalDateTime?,
                               val mediaType: MediaType)
    : BufferedFile(bytes) {
    companion object {
        private val logger = KotlinLogging.logger {  }
        fun imageFromInputStream(inputStream : InputStream, fileName: String) : BufferedFileWithMetaData {

            val byteData = inputStream.readBytes()
            var creationDate : LocalDateTime? = null
            ByteArrayInputStream(byteData).use { tmpInputStream ->
                try {
                    creationDate = readImageCreationDate(tmpInputStream)?.let {
                        LocalDateTime.ofInstant(it.toInstant(), ZoneId.systemDefault())
                    }
                } catch (e : Exception) {
                    logger.error(String.format("error reading metadata from file %s", fileName), e)
                }
            }
            return BufferedFileWithMetaData(byteData, creationDate, MediaType.Image)
        }

        fun videoFromInputStream(inputStream : InputStream, fileName: String) : BufferedFileWithMetaData {

            val byteData = inputStream.readBytes()
            var creationDate : LocalDateTime? = null
            ByteArrayInputStream(byteData).use { tmpInputStream ->
                try {
                    creationDate = readImageCreationDate(tmpInputStream)?.let {
                        LocalDateTime.ofInstant(it.toInstant(), ZoneId.systemDefault())
                    }
                } catch (e : Exception) {
                    logger.error(String.format("error reading metadata from file %s", fileName), e)
                }
            }
            return BufferedFileWithMetaData(byteData, creationDate, MediaType.Video)
        }

        private fun readImageCreationDate(inputStream: InputStream): Date? {
            val metadata = readImageMetaData(inputStream)
            var creationDate : Date? = null
            if(metadata != null) {
                creationDate = metadata.tryGetTime(IccDirectory::class.java, IccDirectory.TAG_PROFILE_DATETIME)
                if(creationDate == null) {
                    creationDate = metadata.tryGetTime(ExifIFD0Directory::class.java, ExifIFD0Directory.TAG_DATETIME)
                }
                if(creationDate == null) {
                    creationDate = metadata.tryGetTime(Mp4Directory::class.java, Mp4Directory.TAG_CREATION_TIME);
                }
                if(creationDate == null) {
                    //metadata?.logMetaData()
                }
            }
            return creationDate
        }

        private fun <T : Directory> Metadata.tryGetTime(directoryClass: Class<T>, tag : Int) : Date?{
            val directory = getFirstDirectoryOfType(directoryClass)
            return directory?.getDate(tag)
        }

        private fun readImageMetaData(inputStream: InputStream): Metadata? {
            var metadata: Metadata? = null
            metadata = ImageMetadataReader.readMetadata(inputStream)
            //metadata.logMetaData()
            return metadata
        }

        private fun Metadata.logMetaData() {
            directories.forEach {directory ->
                directory.tags.forEach {
                    logger.info("Directory {}; tagName {}, tagType {}, value {}",
                            directory.javaClass.simpleName, it.tagName, it.tagType, directory.getString(it.tagType))
                }
            }
        }
    }
}