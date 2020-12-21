package com.notebook.config.redis;

import com.notebook.config.storage.FileStorageService;
import com.notebook.service.StorageService;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.listener.KeyExpirationEventMessageListener;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.stereotype.Component;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Project: notebook
 * File: RedisStorageTempListener
 *
 * @author evan
 * @date 2020/11/9
 */
@Component
public class RedisStorageTempListener extends KeyExpirationEventMessageListener {
    private final FileStorageService fileStorageService;
    private static final Pattern STORAGE_COMPILE_PATTERN = Pattern.compile("^" + StorageService.REDIS_STORAGE_NAMESPACE + "(.*)$");

    public RedisStorageTempListener(RedisMessageListenerContainer listenerContainer,
                                    FileStorageService fileStorageService) {
        super(listenerContainer);
        this.fileStorageService = fileStorageService;
    }

    @Override
    public void onMessage(Message message, byte[] pattern) {
        String expiredKey = message.toString();
        if (expiredKey.startsWith(StorageService.REDIS_STORAGE_NAMESPACE)) {
            Matcher matcher = STORAGE_COMPILE_PATTERN.matcher(expiredKey);
            String validKey = null;
            while (matcher.find()) {
                validKey = matcher.group(1);
            }
            if (validKey != null) {
                fileStorageService.delete(validKey);
            }
        }
    }
}
