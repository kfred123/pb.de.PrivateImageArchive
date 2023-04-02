package pia.logic.tools

import org.mp4parser.IsoFile
import java.io.File
import java.io.InputStream
import java.nio.channels.Channels
import java.nio.channels.FileChannel
import java.nio.channels.ReadableByteChannel
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.util.*

class VideoInfoReader {
    fun readVideoInfo(file : File) : VideoInfo? {
        var videoInfo : VideoInfo
        IsoFile(file).use {
            videoInfo = VideoInfo(it.movieBox)
        }
        return videoInfo
    }
}