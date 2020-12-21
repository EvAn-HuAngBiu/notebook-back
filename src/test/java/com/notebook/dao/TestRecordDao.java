package com.notebook.dao;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.notebook.dao.mapper.RecordMapper;
import com.notebook.service.StorageService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Project: notebook
 * File: TestRecordDao
 *
 * @author evan
 * @date 2020/11/9
 */
@SpringBootTest
@RunWith(SpringRunner.class)
public class TestRecordDao {
    @Autowired
    private RecordMapper recordMapper;

    @Autowired
    private StringRedisTemplate template;

    @Test
    public void serializeArray() throws JsonProcessingException {
        List<String> a = List.of("a.jpeg", "b.jpeg", "c.jpeg");
        ObjectMapper mapper = new ObjectMapper();
        System.out.println(mapper.writeValueAsString(a));
    }

    @Test
    public void deserializeArray() throws JsonProcessingException {
        String json = "[\"a.jpeg\",\"b.jpeg\",\"c.jpeg\"]";
        ObjectMapper mapper = new ObjectMapper();
        List<String> list = mapper.readValue(json, List.class);
        list.forEach(l -> System.out.println(l.getClass()));
    }

    @Test
    public void testExpireListener() throws InterruptedException {
        template.opsForValue().set(StorageService.REDIS_STORAGE_NAMESPACE + "123", "", 15, TimeUnit.SECONDS);
        Thread.sleep(60 * 1000);
    }

    @Test
    public void testString() {
        String expStr = "storage:token:123";
        Pattern pattern = Pattern.compile("^storage:token:(.*)$");
        Matcher matcher = pattern.matcher(expStr);
        while (matcher.find()) {
            System.out.println(matcher.group(1));
        }
    }
}
