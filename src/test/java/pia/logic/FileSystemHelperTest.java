package pia.logic;

import org.junit.Assert;
import org.junit.Test;
import pia.filesystem.FileSystemHelper;

public class FileSystemHelperTest {
    @Test
    public void testGetFileExtension() {
        FileSystemHelper fileSystemHelper = new FileSystemHelper();
        Assert.assertEquals("png", fileSystemHelper.getFileExtension("test.png"));
        Assert.assertEquals("txt", fileSystemHelper.getFileExtension("/home/someone/somewhere/text.txt"));
        Assert.assertEquals("", fileSystemHelper.getFileExtension("/home/someone/somewhere/"));
    }
}
