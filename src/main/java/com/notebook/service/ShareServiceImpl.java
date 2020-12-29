package com.notebook.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.notebook.domain.ShareDo;
import com.notebook.dao.mapper.ShareMapper;
import com.notebook.service.ShareService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author evan
 * @since 2020-11-09
 */
@Service
public class ShareServiceImpl extends ServiceImpl<ShareMapper, ShareDo> implements ShareService {
    @Override
    public synchronized boolean increaseShareCollect(Integer shareId) {
        ShareDo share = this.getOne(new LambdaQueryWrapper<ShareDo>().select(ShareDo::getCollectCnt)
                .eq(ShareDo::getShareId, shareId));
        share.setCollectCnt(Optional.ofNullable(share.getCollectCnt()).orElse(0) + 1);
        return this.update(share, new LambdaQueryWrapper<ShareDo>().eq(ShareDo::getShareId, shareId));
    }

    @Override
    public synchronized boolean decreaseShareCollect(Integer shareId) {
        ShareDo share = this.getOne(new LambdaQueryWrapper<ShareDo>().select(ShareDo::getCollectCnt)
                .eq(ShareDo::getShareId, shareId));
        share.setCollectCnt(Optional.ofNullable(share.getCollectCnt()).orElse(0) - 1);
        return this.update(share, new LambdaQueryWrapper<ShareDo>().eq(ShareDo::getShareId, shareId));
    }

    @Override
    public Integer getUserIdByShareId(Integer shareId) {
        return this.getOne(new LambdaQueryWrapper<ShareDo>().select(ShareDo::getUserId)
                .eq(ShareDo::getShareId, shareId).last("LIMIT 1")).getUserId();
    }
}
