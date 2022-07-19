package pia.filesystem

import java.io.BufferedInputStream
import java.io.InputStream

open class BufferedFile(val bytes : ByteArray) {
    companion object {
        fun fromInputStream(inputStream : InputStream) : BufferedFile {
            return BufferedFile(inputStream.readBytes())
        }
    }
}