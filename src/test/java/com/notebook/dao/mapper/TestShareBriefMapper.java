package com.notebook.dao.mapper;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * Project: notebook
 * File: TestShareBriefMapper
 *
 * @author evan
 * @date 2020/11/11
 */
@SpringBootTest
@RunWith(SpringRunner.class)
public class TestShareBriefMapper {
    @Autowired
    private ShareVoMapper mapper;

    @Test
    public void testListAllShareUserInfo() {
        System.out.println(mapper.selectShareAndUserInfo());
    }

    @Test
    public void testSelectShareUserInfoById() {
        System.out.println(mapper.selectShareAndUserInfoById(1));
    }

    @Test
    public void testSelectShareUserInfoOrderByHot() {
        System.out.println(mapper.selectShareAndUserInfoOrderByHot(1, 0, 2));
    }

    @Test
    public void testGetRecord() {
        System.out.println(mapper.selectBriefRecordByShareId(1));
    }
}
