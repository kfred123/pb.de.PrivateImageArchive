package pia.filesystem

import pia.tools.Configuration
import java.io.*
import java.nio.file.Path

class FileSystemHelper {

    fun writeFileToDisk(bufferedFile: BufferedFile, fileName: String) : File {
        val pathToDir = Path.of(Configuration.getPathToFileStorage())
        val path =
            Path.of(Configuration.getPathToFileStorage(), fileName)
        pathToDir.toFile().mkdirs()
        val file = path.toFile()
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