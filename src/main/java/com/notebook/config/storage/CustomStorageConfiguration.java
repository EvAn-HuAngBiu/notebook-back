package com.notebook.config.storage;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Project: notebook
 * File: CustomStorageConfiguration
 *
 * @author evan
 * @date 2020/11/8
 */
@Configuration
@EnableConfigurationProperties(StorageProperties.class)
public class CustomStorageConfiguration {
    public static final String LOCAL_STORAGE_FLAG = "local";
    public static final String ALIYUN_STORAGE_FLAG = "aliyun";

    private final StorageProperties properties;

    public CustomStorageConfiguration(StorageProperties properties) {
        this.properties = properties;
    }

    @Bean
    public LocalStorage localStorage() {
        return new LocalStorage(properties.getLocal().getStoragePath(),
                properties.getLocal().getUrlPrefix());
    }

    @Bean
    public AliyunStorage aliyunStorage() {
        StorageProperties.Aliyun aliyun = properties.getAliyun();
        return new AliyunStorage(aliyun.getEndpoint(), aliyun.getAccessKeyId(),
                aliyun.getAccessKeySecret(), aliyun.getBucketName());
    }

    @Bean
    public FileStorageService fileStorageService() {
        FileStorageService fileStorageService = new FileStorageService();
        String active = properties.getActive();
        if (active.equalsIgnoreCase(ALIYUN_STORAGE_FLAG)) {
            fileStorageService.setActive(ALIYUN_STORAGE_FLAG);
            fileStorageService.setStorage(aliyunStorage());
        } else {
            fileStorageService.setActive(LOCAL_STORAGE_FLAG);
            fileStorageService.setStorage(localStorage());
        }
        return fileStorageService;
    }
}
