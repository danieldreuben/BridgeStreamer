package com.test.async.streamer;

/**
 * Functional interface for download producers.
 * Implementations receive a DownloadCommand and a DownloadResult.
 */
@FunctionalInterface
public interface DownloadProducer {

    /**
     * Produce the download output based on the command.
     *
     * @param command the download command with parameters
     * @param result  the target where data and metadata should be written
     * @throws Exception if producing fails
     */
    void produce(DownloadCommand command, DownloadResult result) throws Exception;
}

