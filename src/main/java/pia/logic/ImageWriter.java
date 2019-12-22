package pia.logic;

import pia.database.DataBaseConnection;
import pia.database.Database;
import pia.database.DbException;
import pia.database.model.archive.Image;
import pia.exceptions.CreateHashException;
import pia.filesystem.BufferedFile;
import pia.filesystem.FileSystemHelper;
import pia.tools.FileHash;
import pia.tools.Logger;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;

public class ImageWriter {
    private final Logger logger = new Logger(ImageWriter.class);

    public void addImage(BufferedFile bufferedFile, String fileName) throws IOException, CreateHashException {
        Optional<String> hash = FileHash.createHash(bufferedFile);
        if(hash.isPresent()) {
            Image image = new Image();
            FileSystemHelper fileSystemHelper = new FileSystemHelper();
            String fileOnDisk = image.getId().toString() + "." + fileSystemHelper.getFileExtension(fileName);
            File file = fileSystemHelper.writeFileToDisk(bufferedFile, fileOnDisk);
            if (file.exists()) {
                image.setSha256Hash(hash.get());
                image.setOriginalFileName(fileName);
                image.setPathToFileOnDisk(file.getAbsolutePath());
                Database.getConnection().insertObject(image);
            } else {
                logger.error("error writing file to disk");
            }
        } else {
            throw new CreateHashException();
        }
    }

    public boolean deleteImage(Image image) {
        boolean deleted = false;
        String imageFile = image.getPathToFileOnDisk();
        FileSystemHelper fileSystemHelper = new FileSystemHelper();
        boolean deleteFromDb = true;
        if (fileSystemHelper.fileExists(imageFile)) {
            if (!fileSystemHelper.deleteFileFromDisk(imageFile)) {
                deleteFromDb = false;
                logger.error("could not imagefile from disk %s, db-entry will not be deleted", imageFile);
            }
        } else {
            logger.warn("deleting image %s, file %s does not exist", image.getId(), image.getPathToFileOnDisk());
        }
        if (deleteFromDb) {
            if (Database.getConnection().deleteObject(image)) {
                deleted = true;
            } else {
                logger.error("could not delete the imageobject %s from database", image.getId());
            }
        }
        return deleted;
    }
}
