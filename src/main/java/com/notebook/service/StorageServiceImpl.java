package com.notebook.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.notebook.dao.mapper.StorageMapper;
import com.notebook.domain.StorageDo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * <p>
 * 文件存储表 服务实现类
 * </p>
 *
 * @author evan
 * @since 2020-11-08
 */
@Service
public class StorageServiceImpl extends ServiceImpl<StorageMapper, StorageDo> implements StorageService {
    private final StringRedisTemplate template;

    public StorageServiceImpl(StringRedisTemplate template) {
        this.template = template;
    }

    @Override
    public void deleteTempCache(String... keys) {
        this.template.delete(Arrays.stream(keys).map(s -> REDIS_STORAGE_NAMESPACE + s)
                .collect(Collectors.toList()));
    }

    @Override
    public void addTempCache(String... keys) {
        for (String key : keys) {
            this.template.opsForValue().set(REDIS_STORAGE_NAMESPACE + key, "", 3, TimeUnit.MINUTES);
        }
    }

    @Override
    public StorageDo getByKey(String key) {
        return this.getOne(new QueryWrapper<StorageDo>().eq("key_str", key).last("LIMIT 1"));
    }

    @Override
    public boolean checkKeyExist(String key) {
        return this.getOne(new QueryWrapper<StorageDo>().select("key_str")
                .eq("key_str", key).last("LIMIT 1")) != null;
    }

    @Override
    public boolean deleteByKey(String key) {
        return this.remove(new QueryWrapper<StorageDo>().eq("key_str", key));
    }

    @Override
    public boolean deleteByKeyBatch(List<String> key) {
        return this.remove(new QueryWrapper<StorageDo>().in("key_str", key));
    }

    @Override
    public boolean addStorage(String key, InputStream inputStream, long contentLength,
                              String contentType, String fileName) {
        this.addTempCache(key);

        StorageDo storageInfo = new StorageDo();
        storageInfo.setOriginalName(fileName);
        storageInfo.setFileSize((int) contentLength);
        storageInfo.setFileType(contentType);
        storageInfo.setAddTime(LocalDateTime.now());
        storageInfo.setModifiedTime(LocalDateTime.now());
        storageInfo.setKeyStr(key);
        return this.save(storageInfo);
    }
}
