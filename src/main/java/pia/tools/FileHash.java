package pia.tools;

import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.Optional;

public class FileHash {
    private static final Logger logger = new pia.tools.Logger(FileHash.class);

    public static Optional<String> createHash(InputStream inputStream) throws IOException {
        Optional<String> result = Optional.empty();
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(inputStream.readAllBytes());
            result = Optional.ofNullable(Base64.getEncoder().encodeToString(hash));
        } catch (NoSuchAlgorithmException e) {
            logger.error("cannot get hash instance", e);
        }
        return result;
    }
}
