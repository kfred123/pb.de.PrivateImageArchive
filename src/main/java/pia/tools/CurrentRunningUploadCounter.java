package pia.tools;

import java.util.concurrent.atomic.AtomicInteger;

public class CurrentRunningUploadCounter implements AutoCloseable {
    public static AtomicInteger currentRunningUploads = new AtomicInteger();

    public CurrentRunningUploadCounter() {
        currentRunningUploads.incrementAndGet();
    }
    @Override
    public void close() {
        currentRunningUploads.decrementAndGet();
    }
}
