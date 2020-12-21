package com.notebook.config.storage;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Project: notebook
 * File: StorageProperties
 *
 * @author evan
 * @date 2020/11/8
 */
@Data
@ConfigurationProperties("notebook.storage")
public class StorageProperties {
    private String active;
    private final Local local = new Local();
    private final Aliyun aliyun = new Aliyun();

    @Data
    public static class Local {
        /**
         * 本地存储路径文件夹
         */
        private String storagePath = "";

        /**
         * 访问路径前缀URL
         */
        private String urlPrefix = "";
    }

    @Data
    public static class Aliyun {
        private  String endpoint = "";
        private  String accessKeyId = "";
        private  String accessKeySecret = "";
        private  String bucketName = "";
    }
}

