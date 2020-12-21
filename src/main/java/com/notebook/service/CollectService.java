package com.notebook.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.notebook.domain.CollectDo;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author evan
 * @since 2020-11-16
 */
public interface CollectService extends IService<CollectDo> {
    List<Boolean> checkWhetherUserCollectBatchByShareId(Integer userId, List<Integer> shareIds);

    Page<CollectDo> listPagedCollections(Integer userId, Integer page, Integer size);
}
