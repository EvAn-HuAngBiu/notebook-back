package com.notebook.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.OrderItem;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.notebook.dao.mapper.RecordMapper;
import com.notebook.domain.RecordDo;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

/**
 * <p>
 * 记录信息表 服务实现类
 * </p>
 *
 * @author evan
 * @since 2020-11-08
 */
@Service
public class RecordServiceImpl extends ServiceImpl<RecordMapper, RecordDo> implements RecordService {
    public static final List<OrderItem> SORT_TYPE_ORDER = Collections.unmodifiableList(
            List.of(OrderItem.desc("record_rate"), OrderItem.asc("record_rate"), OrderItem.desc("add_time")
            ,OrderItem.asc("add_time")));

    @Override
    public Page<RecordDo> listPagedRecordByUserIdAndTagId(Integer userId, Integer tagId, Integer page, Integer size, Integer sortType) {
        // Page<RecordDo> pageObj = new Page<>((page - 1) * size, size);
        Page<RecordDo> pageObj = new Page<>(page, size);
        pageObj.addOrder(SORT_TYPE_ORDER.get(sortType));
        LambdaQueryWrapper<RecordDo> query = new LambdaQueryWrapper<>();
        query = query.eq(RecordDo::getUserId, userId);
        if (tagId != 0) {
            query = query.eq(RecordDo::getTagId, tagId);
        }
        return this.page(pageObj, query);
    }

    @Override
    public Page<RecordDo> newListPagedRecord(Integer userId, Integer recordType, Integer tagId, Integer page, Integer size, Integer sortType) {
        Page<RecordDo> pageObj = new Page<>(page, size);
        pageObj.addOrder(SORT_TYPE_ORDER.get(sortType));
        LambdaQueryWrapper<RecordDo> query = new LambdaQueryWrapper<>();
        query = query.eq(RecordDo::getUserId, userId).eq(RecordDo::getRecordType, recordType);
        if (tagId != 0) {
            query = query.eq(RecordDo::getTagId, tagId);
        }
        return this.page(pageObj, query);
    }

    @Override
    public boolean updateEntityById(RecordDo record) {
        return this.update(record, new QueryWrapper<RecordDo>().eq("record_id", record.getRecordId()));
    }
}
