package com.test.async.streamer;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

// 4. Registry for producers
public class DownloadRegistry {
    private final Map<String, DownloadProducer> producers = new HashMap<>();

    public void register(String name, DownloadProducer producer) {
        producers.put(name, producer);
    }

    public Optional<DownloadProducer> find(String name) {
        return Optional.ofNullable(producers.get(name));
    }
}