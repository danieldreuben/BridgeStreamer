package com.test.async.streamer;

import java.io.ByteArrayOutputStream;

// 2. Result (metadata + stream)
public class DownloadResult {
    private final String filename;
    private final ByteArrayOutputStream stream;

    public DownloadResult(String filename) {
        this.filename = filename;
        this.stream = new ByteArrayOutputStream();
    }

    public String getFilename() { return filename; }
    public ByteArrayOutputStream getStream() { return stream; }
}
