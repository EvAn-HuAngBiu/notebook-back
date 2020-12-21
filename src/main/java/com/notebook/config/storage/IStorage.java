package com.notebook.config.storage;

import org.springframework.core.io.Resource;
import org.springframework.scheduling.annotation.Async;

import java.io.InputStream;
import java.nio.file.Path;
import java.util.stream.Stream;

public interface IStorage {
    @Async("uploadThreadPool")
    void store(InputStream inputStream, long contentLength, String contentType, String keyName);

    Stream<Path> loadAll();

    Path load(String keyName);

    Resource loadAsResource(String keyName);

    void delete(String keyName);

    String generateUrl(String keyName);
}
