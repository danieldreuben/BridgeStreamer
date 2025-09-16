package com.test.async.streamer;

import java.util.Map;

/**
 * Command object that represents the client request.
 */
public record DownloadCommand(String name, String user, Map<String, String> params) {
}
