package com.notebook.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.OrderItem;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.notebook.domain.CollectDo;
import com.notebook.dao.mapper.CollectMapper;
import com.notebook.service.CollectService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author evan
 * @since 2020-11-16
 */
@Service
public class CollectServiceImpl extends ServiceImpl<CollectMapper, CollectDo> implements CollectService {

    @Override
    public List<Boolean> checkWhetherUserCollectBatchByShareId(Integer userId, List<Integer> shareIds) {
        List<Boolean> result = new ArrayList<>(shareIds.size());
        shareIds.forEach(s -> {
            result.add(this.baseMapper.selectCount(new LambdaQueryWrapper<CollectDo>()
                    .eq(CollectDo::getUserId, userId).eq(CollectDo::getShareId, s)) != 0);
        });
        return result;
    }

    @Override
    public Page<CollectDo> listPagedCollections(Integer userId, Integer page, Integer size) {
        Page<CollectDo> iPage = new Page<>(page, size);
        return this.page(iPage, new LambdaQueryWrapper<CollectDo>()
                .eq(CollectDo::getUserId, userId));
    }
}
