package com.notebook.controller;

import com.aliyun.oss.OSS;
import com.aliyun.oss.common.utils.BinaryUtil;
import com.aliyun.oss.model.PolicyConditions;
import com.notebook.config.storage.AliyunStorage;
import com.notebook.config.storage.FileStorageService;
import com.notebook.domain.StorageDo;
import com.notebook.service.StorageService;
import com.notebook.util.ReturnCode;
import com.notebook.util.ReturnResult;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Date;

/**
 * <p>
 * 文件存储表 前端控制器
 * </p>
 *
 * @author evan
 * @since 2020-11-08
 */
@Slf4j
@RestController
@RequestMapping("/storage")
public class StorageController {
    private final StorageService storageService;
    private final FileStorageService fileStorageService;
    private final AliyunStorage aliyunStorage;

    public StorageController(StorageService storageService, FileStorageService fileStorageService,
                             AliyunStorage aliyunStorage) {
        this.storageService = storageService;
        this.fileStorageService = fileStorageService;
        this.aliyunStorage = aliyunStorage;
    }

    @PostMapping("/upload")
    public ReturnResult upload(@RequestParam("file") MultipartFile file) {
        String originalFileName = file.getOriginalFilename();
        if (StringUtils.isNoneBlank(originalFileName)) {
            return ReturnResult.newInstance().setCode(ReturnCode.PARAM_NOT_COMPLETE);
        }
        assert originalFileName != null;
        String key = fileStorageService.generateKey(originalFileName);
        try {
            boolean result = storageService.addStorage(key, file.getInputStream(),
                    file.getSize(), file.getContentType(), originalFileName);
            if (!result) {
                return ReturnResult.newInstance().setCode(ReturnCode.INTERNAL_ERROR);
            }
            fileStorageService.store(file.getInputStream(), file.getSize(),
                    file.getContentType(), originalFileName, key);
        } catch (IOException e) {
            log.error("存储文件失败，错误: {}, 文件信息: {}", e.getMessage(), file.toString());
            return ReturnResult.newInstance().setCode(ReturnCode.INTERNAL_ERROR);
        }
        return ReturnResult.newInstance().setCode(ReturnCode.SUCCESS)
                .putData("key", key);
    }

    // TODO: 未登录也可以访问其他人的照片，存在安全性问题
    @GetMapping(value = "/fetch/{key:.+}")
    public Object fetch(@PathVariable String key) {
        StorageDo storage = storageService.getByKey(key);
        if (storage == null) {
            return ReturnResult.newInstance().setCode(ReturnCode.PAGE_NOT_FOUND)
                    .putData("errMsg", String.format("文件记录 [%s] 未找到", key));
        }
        MediaType mediaType = MediaType.parseMediaType(storage.getFileType());
        Resource file = fileStorageService.loadAsResource(key);
        if (file == null) {
            return ReturnResult.newInstance().setCode(ReturnCode.PAGE_NOT_FOUND)
                    .putData("errMsg", String.format("文件 [%s] 未找到", key));
        }
        return ResponseEntity.ok().contentType(mediaType).body(file);
    }

    @GetMapping("/download/{key:.+}")
    public Object download(@PathVariable String key) {
        StorageDo storage = storageService.getByKey(key);
        if (storage == null) {
            return ReturnResult.newInstance().setCode(ReturnCode.PAGE_NOT_FOUND)
                    .putData("errMsg", String.format("文件记录 [%s] 未找到", key));
        }
        MediaType mediaType = MediaType.parseMediaType(storage.getFileType());
        Resource file = fileStorageService.loadAsResource(key);
        if (file == null) {
            return ReturnResult.newInstance().setCode(ReturnCode.PAGE_NOT_FOUND)
                    .putData("errMsg", String.format("文件 [%s] 未找到", key));
        }
        return ResponseEntity.ok().contentType(mediaType).header(HttpHeaders.CONTENT_DISPOSITION,
                "attachment; filename=\"" + file.getFilename() + "\"").body(file);
    }

    @PostMapping("/signature")
    public ReturnResult signatureUpload() {
        OSS oss = aliyunStorage.getOssClient();
        try {
            long expireTime = 180;
            long expireEndTime = System.currentTimeMillis() + expireTime * 1000;
            Date expiration = new Date(expireEndTime);
            PolicyConditions policyConds = new PolicyConditions();
            policyConds.addConditionItem(PolicyConditions.COND_CONTENT_LENGTH_RANGE, 0, 10485760);

            String postPolicy = oss.generatePostPolicy(expiration, policyConds);
            byte[] binaryData = postPolicy.getBytes(StandardCharsets.UTF_8);
            String encodedPolicy = BinaryUtil.toBase64String(binaryData);
            String postSignature = oss.calculatePostSignature(postPolicy);
            return ReturnResult.newInstance().setCode(ReturnCode.SUCCESS)
                    .putData("policy", encodedPolicy)
                    .putData("signature", postSignature)
                    .putData("accessid", aliyunStorage.getAccessKeyId())
                    .putData("url", aliyunStorage.getBaseUrl());
        } catch (Exception e) {
            log.error(e.getMessage());
            return ReturnResult.newInstance().setCode(ReturnCode.INTERNAL_ERROR);
        }
    }
}

