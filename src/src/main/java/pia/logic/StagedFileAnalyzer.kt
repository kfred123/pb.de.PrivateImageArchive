package pia.logic

import kotlinx.coroutines.Runnable
import kotlinx.dnq.query.firstOrNull
import mu.KotlinLogging
import pia.database.Database
import pia.database.model.archive.StagedFile
import pia.database.model.archive.StagedTypeEntity
import pia.filesystem.FileSystemHelper
import pia.logic.tools.ImageInfo
import pia.logic.tools.ImageInfoReader
import pia.logic.tools.VideoInfoReader
import java.io.File
import java.util.concurrent.ScheduledThreadPoolExecutor
import java.util.concurrent.TimeUnit

class StagedFileAnalyzer {
    companion object {
        val logger = KotlinLogging.logger { }
        val Instance = StagedFileAnalyzer()
    }

    var executor = ScheduledThreadPoolExecutor(1)
    var executing = false
    var pause = false

    fun start(delayMs: Long) {
        if (!executing) {
            executor.schedule(
                Runnable { run() },
                delayMs, TimeUnit.MILLISECONDS
            )
        }
    }

    private fun run() {
        try {
            executing = true
            analyzeStagedFiles()
        } catch (e: Throwable) {
            logger.error { e }
        } finally {
            executing = false
        }
    }

    private fun analyzeStagedFiles() {
        while (true) {
            if (WorkBalancer.canRunStagedFileAnalyzer()) {
                val stagedFile = getNextStagedFile()
                if (stagedFile != null) {
                    logger.info { "Start analyzing staged files" }
                    workerLoop()
                    logger.info { "Pause analyzing staged files" }
                }
            }
            Thread.sleep(5000);
        }
    }

    private fun workerLoop() {
        var stagedFile : StagedFile? = null
        do {
            Database.connection.transactional {
                stagedFile = getNextStagedFile()
                if (stagedFile != null) {
                    analyzeStagedFile(stagedFile!!)
                }
                it.commit()
            }
            stagedFile = getNextStagedFile()
        }
        while (stagedFile != null && WorkBalancer.canRunStagedFileAnalyzer())
    }

    private fun analyzeStagedFile(stagedFile: StagedFile) {
        if (stagedFile.pathToFileOnDisk.orEmpty().isNotEmpty()) {
            if (FileSystemHelper().fileExists(stagedFile.pathToFileOnDisk!!)) {
                if (stagedFile.stagedTypeEntity == StagedTypeEntity.Image) {
                    analyzeImage(stagedFile);
                } else if (stagedFile.stagedTypeEntity == StagedTypeEntity.Video) {
                    analyzeVideo(stagedFile)
                }
            } else {
                logger.error { "Stagedfile-Entry, file does not exist: ${stagedFile.pathToFileOnDisk}} " }
                deleteStagedFile(stagedFile)
            }
        } else {
            logger.error { "Stagedfile-Entry without filePath ${stagedFile!!.entityId}" }
            deleteStagedFile(stagedFile!!)
        }
    }

    private fun getNextStagedFile(): StagedFile? {
        return Database.connection.transactional(readonly = true) { transaction ->
            StagedFile.all().firstOrNull()
        }
    }

    private fun analyzeVideo(stagedFile: StagedFile) {
        // ToDo SCHWERWIEGEND: UnknownBox{type=    } might have been truncated by file end. bytesRead=13743 contentSize=1751411818 (added catch already)
        val videoInfo = VideoInfoReader().readVideoInfo(File(stagedFile.pathToFileOnDisk!!))
        val file = VideoWriter().addVideo(stagedFile.pathToFileOnDisk!!, videoInfo!!, stagedFile.originalFileName!!)
        if (file != null) {
            removeStagedFile(stagedFile)
        }
    }

    private fun deleteStagedFile(stagedFile: StagedFile) {
        Database.connection.transactional {
            stagedFile.delete()
             it.commit()
        }
    }

    private fun analyzeImage(stagedFile: StagedFile) {
        var imageInfo: ImageInfo?
        FileSystemHelper().readFileFromDisk(stagedFile.pathToFileOnDisk!!).use { inputStream ->
            imageInfo = ImageInfoReader().readImageInfo(inputStream)
        }

        val file = ImageWriter().addImage(stagedFile.pathToFileOnDisk!!, imageInfo!!, stagedFile.originalFileName!!)
        if (file != null) {
            removeStagedFile(stagedFile)
        }
    }

    private fun removeStagedFile(stagedFile: StagedFile) {
        val pathToFileOnDisk = stagedFile.pathToFileOnDisk!!
        stagedFile.delete()
        FileSystemHelper().deleteFileFromDisk(pathToFileOnDisk)
    }
}