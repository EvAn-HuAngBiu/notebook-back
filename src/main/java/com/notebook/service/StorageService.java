package com.notebook.service;

import com.notebook.domain.StorageDo;
import com.baomidou.mybatisplus.extension.service.IService;

import java.io.InputStream;
import java.util.List;

/**
 * <p>
 * 文件存储表 服务类
 * </p>
 *
 * @author evan
 * @since 2020-11-08
 */
public interface StorageService extends IService<StorageDo> {
    String REDIS_STORAGE_NAMESPACE = "storage:temp:";

    void deleteTempCache(String... keys);

    void addTempCache(String... keys);

    StorageDo getByKey(String key);

    boolean checkKeyExist(String key);

    boolean deleteByKey(String key);

    boolean deleteByKeyBatch(List<String> key);

    boolean addStorage(String key, InputStream inputStream, long contentLength, String contentType, String fileName);
}
