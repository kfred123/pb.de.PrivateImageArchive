package pia.logic

import jetbrains.exodus.kotlin.synchronized
import java.time.Duration
import java.time.Instant

class WorkBalancer {
    companion object {
        private var lastUploadTimeStamp = Instant.now()

        fun startStagingFile() {
            synchronized {
                lastUploadTimeStamp = Instant.now()
            }
        }

        fun endStagingFile() {
            synchronized {
                lastUploadTimeStamp = Instant.now()
            }
        }

        fun canRunStagedFileAnalyzer() : Boolean {
            return Duration.between(lastUploadTimeStamp, Instant.now()).seconds > 20
        }
    }
}