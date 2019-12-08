package pia.logic;

import pia.database.Database;
import pia.database.model.archive.Image;
import pia.exceptions.CreateHashException;
import pia.tools.Configuration;
import pia.tools.FileHash;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.Optional;

public class ImageWriter {
    public void addImage(InputStream imageStream, String fileName) throws IOException, CreateHashException {
        Image image = new Image();
        String newFileName = image.getId().toString() + getFileExtension(fileName);
        Path path = Path.of(Configuration.getPathToFileStorage(), newFileName);
        try(FileOutputStream outputStream = new FileOutputStream(path.toFile())) {
            outputStream.write(imageStream.readAllBytes());
        }

        Optional<String> hash = FileHash.createHash(imageStream);
        if(hash.isPresent()) {
            image.setSha256Hash(hash.get());
            image.setOriginalFileName(fileName);
            image.setPathToFileOnDisk(path.toString());
            Database.getConnection().insertObject(image);
        } else {
            throw new CreateHashException();
        }
    }

    public static String getFileExtension(String fileName) {
        String extension = "";
        int lastDot = fileName.lastIndexOf(".");
        if(lastDot >= 0) {
            extension = fileName.substring(lastDot + 1);
        }
        return extension;
    }
}
