package com.notebook.service;

import com.notebook.domain.TagDo;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 * 标签表 服务类
 * </p>
 *
 * @author evan
 * @since 2020-11-05
 */
public interface TagService extends IService<TagDo> {
    List<TagDo> getAllTag();

    TagDo getTagById(Integer tagId);
}
