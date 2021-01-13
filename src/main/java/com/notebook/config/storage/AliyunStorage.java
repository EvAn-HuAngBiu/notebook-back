package com.notebook.config.storage;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.model.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;

import java.io.InputStream;
import java.net.URL;
import java.nio.file.Path;
import java.util.stream.Stream;

/**
 * Project: notebook
 * File: AliyunStorage
 *
 * @author evan
 * @date 2020/11/8
 */
@Slf4j
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AliyunStorage implements IStorage {
    private String endpoint;
    private String accessKeyId;
    private String accessKeySecret;
    private String bucketName;

    /**
     * 获取阿里云OSS客户端对象
     */
    public OSS getOssClient() {
        return new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);
    }

    public String getBaseUrl() {
        return "https://" + bucketName + "." + endpoint + "/";
    }

    /**
     * 阿里云OSS对象存储小文件上传, 最大支持5GB，建议上传小于20MB的文件
     */
    @Override
    public void store(InputStream inputStream, long contentLength, String contentType, String keyName) {
        OSS ossClient = null;
        try {
            ossClient = getOssClient();
            ObjectMetadata objectMetadata = new ObjectMetadata();
            objectMetadata.setContentLength(contentLength);
            objectMetadata.setContentType(contentType);
            PutObjectRequest putObjectRequest = new PutObjectRequest(bucketName, keyName, inputStream, objectMetadata);
            PutObjectResult putObjectResult = ossClient.putObject(putObjectRequest);
            if (putObjectResult == null) {
                log.error("Cannot upload file {}", keyName);
                throw new RuntimeException(String.format("Cannot upload file %s, cause result is null", keyName));
            }
        } catch (Exception e) {
            throw new RuntimeException("Cannot store file " + keyName, e);
        } finally {
            if (ossClient != null) {
                ossClient.shutdown();
            }
        }
    }

    @Override
    public Stream<Path> loadAll() {
        throw new UnsupportedOperationException("Aliyun OSS doesn't support load as a Path object");
    }

    @Override
    public Path load(String keyName) {
        throw new UnsupportedOperationException("Aliyun OSS doesn't support load as a Path object");
    }

    @Override
    public Resource loadAsResource(String keyName) {
        try {
            // 私有模式下使用流读取，安全性高但是效率低
            // OSS ossClient = getOSSClient();
            // OSSObject ossObject = ossClient.getObject(bucketName, keyName);
            // Resource resource = new InputStreamResource(ossObject.getObjectContent());

            // 公共读模式下使用URL读取，效率较高
            URL url = new URL(generateUrl(keyName));
            Resource resource = new UrlResource(url);
            if (resource.exists() || resource.isReadable()) {
                return resource;
            } else {
                return null;
            }
        } catch (Exception e) {
            log.error("Cannot load file {}, error is {}", keyName, e);
            return null;
        }
    }

    @Override
    public void delete(String keyName) {
        try {
            getOssClient().deleteObject(bucketName, keyName);
        } catch (Exception e) {
            log.error("Cannot delete file {}, error is {}", keyName, e);
        }
    }

    @Override
    public String generateUrl(String keyName) {
        return getBaseUrl() + keyName;
    }
}
