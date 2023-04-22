package pia.logic

import mu.KotlinLogging
import org.joda.time.DateTime
import pia.database.Database
import pia.database.model.archive.StagedFile
import pia.database.model.archive.StagedType
import pia.database.model.archive.StagedTypeEntity
import pia.filesystem.FileSystemHelper
import pia.tools.CurrentRunningUploadUtil
import java.io.File
import java.io.InputStream
import java.util.*

class FileStager {
    private val logger = KotlinLogging.logger {  }

    companion object {
        val StageDir = "staged"
    }

    fun stageFile(inputStream : InputStream, originalFileName : String, stagedType: StagedType) {
        CurrentRunningUploadUtil().use {
            StagedFileAnalyzer.Instance.stop()
            Database.connection.transactional { transaction ->
                val fileSystemHelper = FileSystemHelper()
                val fileOnDisk = "${UUID.randomUUID()}_${File(originalFileName).normalize().name}"
                val file = fileSystemHelper.writeFileToDisk(fileOnDisk, inputStream, StageDir, getFolderName(stagedType))
                if(file.exists()) {
                    StagedFile.new {
                        this.originalFileName = originalFileName
                        pathToFileOnDisk = file.absolutePath
                        creationTime = DateTime.now()
                        this.stagedTypeEntity = StagedTypeEntity.get(stagedType)
                    }
                } else {
                    logger.error("error writing file to disk")
                    throw java.lang.Exception("error writing file to disk")
                }
            }
        }
        StagedFileAnalyzer.Instance.start(10000)
    }

    private fun getFolderName(stagedTypeEntity: StagedType) : String {
        return when(stagedTypeEntity) {
            StagedType.Image -> "image"
            StagedType.Video -> "video"
            else -> throw Exception("unssupported stagedType")
        }
    }
}