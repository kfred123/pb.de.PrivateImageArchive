package pia.tools;

import java.util.concurrent.atomic.AtomicInteger;

public class CurrentRunningUploadUtil implements AutoCloseable {
    public static AtomicInteger currentRunningUploads = new AtomicInteger();

    public CurrentRunningUploadUtil() {
        currentRunningUploads.incrementAndGet();
    }
    @Override
    public void close() {
        currentRunningUploads.decrementAndGet();
    }

    public static boolean hasRunningUploads() {
        return currentRunningUploads.get() > 0;
    }
}
