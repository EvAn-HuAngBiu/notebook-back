package com.notebook.dao.mapper;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.notebook.domain.LikeDo;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Project: notebook
 * File: TestLikeMapper
 *
 * @author evan
 * @date 2020/11/14
 */
@SpringBootTest
@RunWith(SpringRunner.class)
public class TestLikeMapper {
    @Autowired
    private LikeMapper likeMapper;

    @Test
    public void testMapperAvailable() {
        Assert.assertNotNull(likeMapper);
    }

    @Test
    public void testLoadShareLikedUsers() {
        List<LikeDo> likes = this.likeMapper.selectList(new LambdaQueryWrapper<LikeDo>()
                .select(LikeDo::getShareId, LikeDo::getUserId));
        likes.stream().collect(Collectors.groupingBy(l -> l.getShareId().toString(),
                Collectors.mapping(l -> l.getUserId().toString(), Collectors.toSet())))
                .forEach((k, v) -> System.out.println(k + ": " + v));
    }

    @Test
    public void testUserLikedCount() {
        System.out.println(this.likeMapper.selectUserLikedCount());
    }

    @Test
    public void testGetUserWhoLikeMe() {
        this.likeMapper.selectUserWhoLikedMe().stream()
                .collect(Collectors.groupingBy(m -> ((Integer) m.get("beliked")).toString(),
                        Collectors.mapping(m -> ((Integer) m.get("like")).toString(), Collectors.toSet())))
                .forEach((k, v) -> System.out.println(k + ": " + v));
    }
}
