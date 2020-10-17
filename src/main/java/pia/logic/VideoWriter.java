package pia.logic;

import pia.database.Database;
import pia.database.model.archive.Video;
import pia.exceptions.CreateHashException;
import pia.filesystem.BufferedFileWithMetaData;
import pia.filesystem.FileSystemHelper;
import pia.tools.FileHash;
import pia.tools.Logger;

import java.io.File;
import java.io.IOException;
import java.util.Optional;

public class VideoWriter {
    private final Logger logger = new Logger(VideoWriter.class);

    public void addVideo(BufferedFileWithMetaData bufferedFile, String fileName) throws IOException, CreateHashException {
        Optional<String> hash = FileHash.createHash(bufferedFile);
        if(hash.isPresent()) {
            Video video = new Video();
            FileSystemHelper fileSystemHelper = new FileSystemHelper();
            String fileOnDisk = video.getId().toString() + "." + fileSystemHelper.getFileExtension(fileName);
            File file = fileSystemHelper.writeFileToDisk(bufferedFile, fileOnDisk);
            if (file.exists()) {
                video.setSha256Hash(hash.get());
                video.setOriginalFileName(fileName);
                video.setPathToFileOnDisk(file.getAbsolutePath());
                video.setCreationTime(bufferedFile.getCreationDate());
                Database.getConnection().insertObject(video);
            } else {
                logger.error("error writing file to disk");
            }
        } else {
            throw new CreateHashException();
        }
    }

    public boolean deleteVideo(Video video) {
        boolean deleted = false;
        String videoFile = video.getPathToFileOnDisk();
        FileSystemHelper fileSystemHelper = new FileSystemHelper();
        boolean deleteFromDb = true;
        if (fileSystemHelper.fileExists(videoFile)) {
            if (!fileSystemHelper.deleteFileFromDisk(videoFile)) {
                deleteFromDb = false;
                logger.error("could not delete videofile from disk %s, db-entry will not be deleted", videoFile);
            }
        } else {
            logger.warn("deleting video %s, file %s does not exist", video.getId(), video.getPathToFileOnDisk());
        }
        if (deleteFromDb) {
            if (Database.getConnection().deleteObject(video)) {
                deleted = true;
            } else {
                logger.error("could not delete the videoobject %s from database", video.getId());
            }
        }
        return deleted;
    }
}
