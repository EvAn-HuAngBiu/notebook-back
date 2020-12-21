package com.notebook.config;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.HashMap;
import java.util.Map;

/**
 * Project: notebook
 * File: TestSendMessage
 *
 * @author evan
 * @date 2020/12/18
 */
@SpringBootTest
@RunWith(SpringRunner.class)
public class TestSendMessage {
    @Autowired
    private RabbitTemplate template;

    @Test
    public void testSendMessage() {
        Map<String, Object> map = new HashMap<>();
        map.put("image", "https://www.baidu.com");
        template.convertAndSend("audit", "image", map);
    }
}
