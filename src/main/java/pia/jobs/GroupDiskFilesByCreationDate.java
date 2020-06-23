package pia.jobs;

import com.drew.imaging.ImageMetadataReader;
import com.drew.imaging.ImageProcessingException;
import com.drew.metadata.Metadata;
import com.drew.metadata.exif.ExifSubIFDDirectory;
import org.eclipse.jetty.util.thread.Scheduler;
import org.mongodb.morphia.query.Meta;
import pia.filesystem.FileSystemHelper;
import pia.logic.ImageReader;
import pia.tools.Configuration;
import pia.tools.Logger;

import javax.swing.text.html.Option;
import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.Optional;

public class GroupDiskFilesByCreationDate implements Runnable {
    private static final Logger logger = new Logger(GroupDiskFilesByCreationDate.class);
    public static void runJob() {
        Thread thread = new Thread(new GroupDiskFilesByCreationDate());
        thread.start();
    }

    @Override
    public void run() {
        //Doch nicht als Job sondern einfach direkt beim Upload schon richtig einsortieren...
        String fileStorage = Configuration.getPathToFileStorage();
        File storage = new File(fileStorage);
        for(File file : storage.listFiles()) {
            if(!file.isDirectory()) {
                categorizeFile(file);
            }
        }
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            logger.error("error", e);
        }
    }

    private void categorizeFile(File file) {
        logger.info("found uncategorized file %s", file.getName());
        Optional<Date> creationDate = readImageCreationDate(file);
    }

    private Optional<Date> readImageCreationDate(File file) {
        Optional<Date> creationDate = Optional.empty();
        Optional<Metadata> metadata = readImageMetaData(file);
        if(metadata.isPresent()) {
            ExifSubIFDDirectory directory = metadata.get().getFirstDirectoryOfType(ExifSubIFDDirectory.class);
            creationDate = Optional.ofNullable(directory.getDate(ExifSubIFDDirectory.TAG_DATETIME_ORIGINAL));
        }
        return creationDate;
    }

    private Optional<Metadata> readImageMetaData(File file) {
        Optional<Metadata> metadata = Optional.empty();
        try {
             metadata = Optional.ofNullable(ImageMetadataReader.readMetadata(file));
        } catch (ImageProcessingException | IOException e) {
            logger.error("error reading metadata from file %s", e);
        }
        return metadata;
    }
}
