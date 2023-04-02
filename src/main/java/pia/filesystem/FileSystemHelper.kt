package pia.filesystem

import pia.tools.Configuration
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.InputStream
import java.nio.file.Files
import java.nio.file.Paths
import java.time.Month
import java.time.format.TextStyle
import java.util.*
import kotlin.io.path.Path

class FileSystemHelper {
    fun moveFileToArchive(sourceFilePath : String, fileName: String, year : Int, month : Month, mediaType: MediaType) : File {
        val pathToDir =
            Path(Configuration.getPathToFileStorage(), year.toString(), month.getDisplayName(TextStyle.SHORT, Locale.ENGLISH),
                mediaType.name)
        pathToDir.toFile().mkdirs()
        val targetPath = Path(pathToDir.toString(), fileName)
        Files.move(Paths.get(sourceFilePath), targetPath)
        return targetPath.toFile()
    }

    fun writeFileToDisk(fileName : String, inputStream: InputStream, vararg subFolder : String) : File {
        val pathToDir =
            Path(Configuration.getPathToFileStorage(), *subFolder)
        pathToDir.toFile().mkdirs()
        val file = Path(pathToDir.toString(), fileName).toFile()
        FileOutputStream(file).use { outputStream -> inputStream.copyTo(outputStream) }
        return file
    }

    fun readFileFromDisk(pathToFile: String): InputStream {
        val file = File(pathToFile)
        return FileInputStream(file)
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