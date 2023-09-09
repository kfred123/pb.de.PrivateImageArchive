package pia.tools

import mu.KotlinLogging
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException
import java.util.*

object FileHash {
    private val logger = KotlinLogging.logger {  }
    /*fun createHash(file: BufferedFile): Optional<String> {
        var result: Optional<String> = Optional.empty()
        try {
            val digest = MessageDigest.getInstance("SHA-256")
            val hash = digest.digest(file.bytes)
            result = Optional.ofNullable(Base64.getEncoder().encodeToString(hash))
        } catch (e: NoSuchAlgorithmException) {
            logger.error("cannot get hash instance", e)
        }
        return result
    }*/
}