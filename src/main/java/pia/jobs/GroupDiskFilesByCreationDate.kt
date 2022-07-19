package pia.jobs

import com.drew.imaging.ImageMetadataReader
import com.drew.imaging.ImageProcessingException
import com.drew.metadata.Metadata
import com.drew.metadata.exif.ExifSubIFDDirectory
import mu.KotlinLogging
import pia.tools.Configuration
import java.io.File
import java.io.IOException
import java.util.*

class GroupDiskFilesByCreationDate : Runnable {
    override fun run() {
        //Doch nicht als Job sondern einfach direkt beim Upload schon richtig einsortieren...
        val fileStorage = Configuration.getPathToFileStorage()
        val storage = File(fileStorage)
        for (file in storage.listFiles()) {
            if (!file.isDirectory) {
                categorizeFile(file)
            }
        }
        try {
            Thread.sleep(1000)
        } catch (e: InterruptedException) {
            logger.error("error", e)
        }
    }

    private fun categorizeFile(file: File) {
        logger.info("found uncategorized file %s", file.name)
        val creationDate = readImageCreationDate(file)
    }

    private fun readImageCreationDate(file: File): Optional<Date> {
        var creationDate = Optional.empty<Date>()
        val metadata = readImageMetaData(file)
        if (metadata.isPresent) {
            val directory = metadata.get().getFirstDirectoryOfType(
                ExifSubIFDDirectory::class.java
            )
            creationDate = Optional.ofNullable(directory.getDate(ExifSubIFDDirectory.TAG_DATETIME_ORIGINAL))
        }
        return creationDate
    }

    private fun readImageMetaData(file: File): Optional<Metadata> {
        var metadata = Optional.empty<Metadata>()
        try {
            metadata = Optional.ofNullable(ImageMetadataReader.readMetadata(file))
        } catch (e: ImageProcessingException) {
            logger.error("error reading metadata from file %s", e)
        } catch (e: IOException) {
            logger.error("error reading metadata from file %s", e)
        }
        return metadata
    }

    companion object {
        private val logger = KotlinLogging.logger {  }
        fun runJob() {
            val thread = Thread(GroupDiskFilesByCreationDate())
            thread.start()
        }
    }
}