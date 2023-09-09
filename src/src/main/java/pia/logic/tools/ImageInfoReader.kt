package pia.logic.tools

import com.drew.imaging.ImageMetadataReader
import com.drew.metadata.Directory
import com.drew.metadata.Metadata
import com.drew.metadata.exif.ExifIFD0Directory
import com.drew.metadata.icc.IccDirectory
import com.drew.metadata.mp4.Mp4Directory
import mu.KotlinLogging
import pia.tools.DateTools
import java.awt.Image
import java.io.InputStream
import java.time.LocalDateTime
import java.util.*

public class ImageInfoReader() {
    companion object {
        val logger = KotlinLogging.logger {  }
    }

    fun readImageInfo(inputStream: InputStream): ImageInfo? {
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
        val imageInfo = if(creationDate != null) {
            ImageInfo(DateTools.toLocalDateTime(creationDate))
        } else {
            ImageInfo(LocalDateTime.MIN)
        }
        return imageInfo
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