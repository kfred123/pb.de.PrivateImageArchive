package pia.filesystem

import pia.tools.Configuration
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.InputStream
import java.nio.file.Path
import java.time.format.TextStyle
import java.util.*

class FileSystemHelper {

    fun writeFileToDisk(bufferedFile: BufferedFileWithMetaData, fileName: String) : File {
        var year = "unknownyear"
        var month = "unknownmonth"
        if(bufferedFile.creationDate != null) {
            year = bufferedFile.creationDate.year.toString()
            month = bufferedFile.creationDate.month.getDisplayName(TextStyle.SHORT, Locale.ENGLISH)
        }
        val pathToDir =
            Path.of(Configuration.getPathToFileStorage(), year, month, bufferedFile.mediaType.name)
        pathToDir.toFile().mkdirs()
        val file = Path.of(pathToDir.toString(), fileName).toFile()
        FileOutputStream(file).use { outputStream -> outputStream.write(bufferedFile.bytes) }
        return file
    }

    fun readFileFromDisk(pathToFile: String) : InputStream {
        val file = File(pathToFile)
        val inputStream = FileInputStream(file)
        return inputStream
    }

    fun getFileExtension(fileName: String): String? {
        var extension: String? = ""
        val lastDot = fileName.lastIndexOf(".")
        if (lastDot >= 0) {
            extension = fileName.substring(lastDot + 1)
        }
        return extension
    }

    fun deleteFileFromDisk(pathToFile: String) : Boolean  {
        val file = File(pathToFile)
        return file.delete()
    }

    fun fileExists(pathToFile: String) : Boolean {
        val file = File(pathToFile)
        return file.exists()
    }
}