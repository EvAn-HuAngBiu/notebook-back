package com.notebook.config;

import com.notebook.config.storage.StorageProperties;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * Project: notebook
 * File: TestStorage
 *
 * @author evan
 * @date 2020/11/8
 */
@SpringBootTest
@RunWith(SpringRunner.class)
public class TestStorage {
    @Autowired
    private StorageProperties storageProperties;

    @Test
    public void testReadStorageProperties() {
        Assert.assertNotNull(storageProperties);
        System.out.println(storageProperties);
    }
}
