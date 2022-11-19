package pia.filesystem

import com.drew.imaging.ImageMetadataReader
import com.drew.metadata.Directory
import com.drew.metadata.Metadata
import com.drew.metadata.exif.ExifIFD0Directory
import com.drew.metadata.icc.IccDirectory
import com.drew.metadata.mp4.Mp4Directory
import mu.KotlinLogging
import pia.jobs.GroupDiskFilesByCreationDate
import pia.logic.tools.ImageInfoReader
import pia.logic.tools.MediaItemInfo
import pia.logic.tools.VideoInfoReader
import java.io.ByteArrayInputStream
import java.io.InputStream
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.*
import javax.print.attribute.standard.Media

class BufferedFileWithMetaData(bytes : ByteArray,
                               val mediaItemInfo : MediaItemInfo,
                               val mediaType: MediaType)
    : BufferedFile(bytes) {
    companion object {
        private val logger = KotlinLogging.logger {  }
        fun imageFromInputStream(inputStream : InputStream, fileName: String) : BufferedFileWithMetaData {

            val byteData = inputStream.readBytes()
            var mediaItemInfo : MediaItemInfo? = null
            ByteArrayInputStream(byteData).use { tmpInputStream ->
                try {
                    mediaItemInfo = ImageInfoReader().readImageInfo(tmpInputStream)
                } catch (e : Exception) {
                    logger.error(String.format("error reading metadata from file %s", fileName), e)
                }
            }
            return BufferedFileWithMetaData(byteData, mediaItemInfo!!, MediaType.Image)
        }

        fun videoFromInputStream(inputStream : InputStream, fileName: String) : BufferedFileWithMetaData {

            val byteData = inputStream.readBytes()
            var mediaItemInfo : MediaItemInfo? = null
            ByteArrayInputStream(byteData).use { tmpInputStream ->
                try {
                    mediaItemInfo = VideoInfoReader().readVideoInfo(tmpInputStream)
                } catch (e : Exception) {
                    logger.error(String.format("error reading metadata from file %s", fileName), e)
                }
            }
            return BufferedFileWithMetaData(byteData, mediaItemInfo!!, MediaType.Video)
        }


    }
}