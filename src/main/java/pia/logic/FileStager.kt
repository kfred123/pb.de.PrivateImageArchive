package pia.logic

import mu.KotlinLogging
import org.glassfish.jersey.process.internal.Stage
import pia.database.Database
import pia.database.model.archive.StagedFile
import pia.filesystem.FileSystemHelper
import java.io.File
import java.io.InputStream
import java.time.LocalDateTime
import java.util.*

class FileStager {
    private val logger = KotlinLogging.logger {  }

    companion object {
        val StageDir = "staged"
    }
    enum class StagedType(var folderName: String) {
        Image("image"),
        Video("video");
    }

    fun stageFile(inputStream : InputStream, originalFileName : String, stagedType: StagedType) {
        StagedFileAnalyzer.Instance.stop()
        Database.connection.transactional {
            val fileSystemHelper = FileSystemHelper()
            val id = UUID.randomUUID()
            val fileOnDisk = "${id}_${File(originalFileName).normalize().name}"
            val file = fileSystemHelper.writeFileToDisk(fileOnDisk, inputStream, StageDir, stagedType.folderName)
            if(file.exists()) {
                StagedFile.new {
                    this.id = id
                    this.originalFileName = originalFileName
                    pathToFileOnDisk = file.absolutePath
                    creationTime = LocalDateTime.now()
                    this.stagedType = stagedType
                }
            } else {
                logger.error("error writing file to disk")
                throw java.lang.Exception("error writing file to disk")
            }
        }
        StagedFileAnalyzer.Instance.start(10000)
    }
}