package com.notebook.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.notebook.dao.mapper.TagMapper;
import com.notebook.domain.TagDo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.PreDestroy;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * <p>
 * 标签表 服务实现类
 * </p>
 *
 * @author evan
 * @since 2020-11-05
 */
@Service
public class TagServiceImpl extends ServiceImpl<TagMapper, TagDo> implements TagService {
    public static final String CACHED_TAG_FLAG = "tag";

    private final StringRedisTemplate template;

    public TagServiceImpl(StringRedisTemplate template) {
        this.template = template;
    }

    @PreDestroy
    public void destroy() {
        this.template.delete(Optional.ofNullable(template.keys(CACHED_TAG_FLAG + "*"))
                .orElse(Collections.emptySet()));
    }

    @Cacheable(cacheNames = CACHED_TAG_FLAG)
    @Override
    public List<TagDo> getAllTag() {
        return this.list(new QueryWrapper<TagDo>().select("tag_id", "tag_name"));
    }

    @Cacheable(cacheNames = CACHED_TAG_FLAG, key = "#tagId")
    @Override
    public TagDo getTagById(Integer tagId) {
        return this.getById(tagId);
    }
}
