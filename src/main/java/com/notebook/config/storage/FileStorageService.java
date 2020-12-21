package com.notebook.config.storage;

import com.notebook.util.CharUtil;
import com.notebook.domain.StorageDo;
import com.notebook.service.StorageService;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.scheduling.annotation.Async;
import org.springframework.transaction.annotation.Transactional;

import java.io.InputStream;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Project: notebook
 * File: FileStorageService
 *
 * @author evan
 * @date 2020/11/8
 */
@Slf4j
public class FileStorageService {
    @Setter
    @Getter
    private String active;

    @Setter
    @Getter
    private IStorage storage;

    @Autowired
    private StorageService storageService;

    /**
     * 存储一个文件对象
     *
     * @param inputStream   文件输入流
     * @param contentLength 文件长度
     * @param contentType   文件类型
     * @param fileName      文件索引名
     */
    public void store(InputStream inputStream, long contentLength, String contentType, String fileName, String key) {
        storage.store(inputStream, contentLength, contentType, key);
    }

    public String generateKey(String originalFilename) {
        int index = originalFilename.lastIndexOf('.');
        String suffix = originalFilename.substring(index);
        String key;
        do {
            key = CharUtil.getRandomString(20) + suffix;
        } while (storageService.checkKeyExist(key));
        return key;
    }

    public Stream<Path> loadAll() {
        return storage.loadAll();
    }

    public Path load(String keyName) {
        return storage.load(keyName);
    }

    public Resource loadAsResource(String keyName) {
        return storage.loadAsResource(keyName);
    }

    public void delete(String keyName) {
        storageService.deleteByKey(keyName);
        storage.delete(keyName);
    }

    public String generateUrl(String keyName) {
        return storage.generateUrl(keyName);
    }

    public List<String> generateUrls(List<String> keyNames) {
        return keyNames.stream().map(this::generateUrl).collect(Collectors.toList());
    }
}
