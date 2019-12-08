package pia.logic;

import org.junit.Assert;
import org.junit.Test;

public class ImageWriterTest {
    @Test
    public void testGetFileExtension() {
        Assert.assertEquals("png", ImageWriter.getFileExtension("test.png"));
        Assert.assertEquals("txt", ImageWriter.getFileExtension("/home/someone/somewhere/text.txt"));
        Assert.assertEquals("", ImageWriter.getFileExtension("/home/someone/somewhere/"));
    }
}
