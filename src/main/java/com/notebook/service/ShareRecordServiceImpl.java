package com.notebook.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.google.common.collect.Sets;
import com.notebook.domain.ShareRecordDo;
import com.notebook.dao.mapper.ShareRecordMapper;
import com.notebook.service.ShareRecordService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author evan
 * @since 2020-11-09
 */
@Service
public class ShareRecordServiceImpl extends ServiceImpl<ShareRecordMapper, ShareRecordDo> implements ShareRecordService {

    @Override
    public Set<Integer> deleteShareRecord(Integer recordId) {
        // 读取删除的shareid
        Set<Integer> shareIds = this.list(new LambdaQueryWrapper<ShareRecordDo>()
                .select(ShareRecordDo::getShareId)
                .eq(ShareRecordDo::getRecordId, recordId))
                .stream()
                .map(ShareRecordDo::getShareId)
                .collect(Collectors.toSet());
        // 删除对应share中的record
        this.remove(new LambdaQueryWrapper<ShareRecordDo>().eq(ShareRecordDo::getRecordId, recordId));
        // 查询非空shareid, 取差集即为没有记录的share
        Set<Integer> nonEmpty = this.list(new LambdaQueryWrapper<ShareRecordDo>()
                .select(ShareRecordDo::getShareId)
                .eq(ShareRecordDo::getDeleted, 0)
                .in(ShareRecordDo::getShareId, shareIds)
                .groupBy(ShareRecordDo::getShareId))
                .stream()
                .map(ShareRecordDo::getShareId)
                .collect(Collectors.toSet());
        return Sets.difference(shareIds, nonEmpty);
    }
}
