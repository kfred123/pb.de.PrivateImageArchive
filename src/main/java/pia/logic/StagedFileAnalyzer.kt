package pia.logic

import kotlinx.coroutines.*
import kotlinx.dnq.query.firstOrNull
import mu.KotlinLogging
import pia.database.Database
import pia.database.model.archive.StagedFile
import pia.database.model.archive.StagedTypeEntity
import pia.filesystem.FileSystemHelper
import pia.logic.tools.ImageInfo
import pia.logic.tools.ImageInfoReader
import pia.logic.tools.VideoInfoReader
import pia.tools.CurrentRunningUploadUtil
import java.io.File

class StagedFileAnalyzer {
    companion object {
        val logger = KotlinLogging.logger {  }
        val Instance = StagedFileAnalyzer()
    }

    var coroutineScope : CoroutineScope? = null

    fun start(delayMs : Long) {
        if(!CurrentRunningUploadUtil.hasRunningUploads()) {
            if (coroutineScope == null || !coroutineScope!!.isActive) {
                runBlocking {
                    coroutineScope = this
                    launch {
                        delay(delayMs)
                        analyzeStagedFiles()
                    }
                }
            }
        }
    }

    fun stop() {
        coroutineScope?.cancel()
    }

    private fun analyzeStagedFiles() {
        Database.connection.beginSession().apply {
            var stagedFile = StagedFile.all().firstOrNull()
            logger.info { "Start analyzing staged files" }
            while (stagedFile != null && coroutineScope!!.coroutineContext.isActive) {
                if(stagedFile.pathToFileOnDisk.orEmpty().isNotEmpty()) {
                    if (stagedFile.stagedTypeEntity == StagedTypeEntity.Image) {
                        analyzeImage(stagedFile);
                    } else if (stagedFile.stagedTypeEntity == StagedTypeEntity.Video) {
                        analyzeVideo(stagedFile)
                    }
                } else {
                    logger.error { "Stagedfile-Entry without filePath ${stagedFile!!.entityId}" }
                    stagedFile.delete()
                }
                stagedFile = StagedFile.all().firstOrNull()
            }
            logger.info { "Done analyzing staged files" }
        }
    }

    private fun analyzeVideo(stagedFile : StagedFile) {
        // ToDo SCHWERWIEGEND: UnknownBox{type=    } might have been truncated by file end. bytesRead=13743 contentSize=1751411818 (added catch already)
        val videoInfo = VideoInfoReader().readVideoInfo(File(stagedFile.pathToFileOnDisk!!))
        val file = VideoWriter().addVideo(stagedFile.pathToFileOnDisk!!, videoInfo!!, stagedFile.originalFileName!!)
        if(file != null) {
            removeStagedFile(stagedFile)
        }
    }

    private fun analyzeImage(stagedFile: StagedFile) {
        var imageInfo : ImageInfo?
        FileSystemHelper().readFileFromDisk(stagedFile.pathToFileOnDisk!!).use { inputStream ->
            imageInfo = ImageInfoReader().readImageInfo(inputStream)
        }

        val file = ImageWriter().addImage(stagedFile.pathToFileOnDisk!!, imageInfo!!, stagedFile.originalFileName!!)
        if(file != null) {
            removeStagedFile(stagedFile)
        }
    }

    private fun removeStagedFile(stagedFile: StagedFile) {
        val pathToFileOnDisk = stagedFile.pathToFileOnDisk!!
        Database.connection.transactional {
            stagedFile.delete()
        }
        FileSystemHelper().deleteFileFromDisk(pathToFileOnDisk)
    }
}