package com.notebook.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.notebook.domain.RecordDo;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 * 记录信息表 服务类
 * </p>
 *
 * @author evan
 * @since 2020-11-08
 */
public interface RecordService extends IService<RecordDo> {
    Page<RecordDo> listPagedRecordByUserIdAndTagId(Integer userId, Integer tagId, Integer page, Integer size, Integer sortType);

    Page<RecordDo> newListPagedRecord(Integer userId, Integer recordType, Integer tagId, Integer page, Integer size, Integer sortType);

    boolean updateEntityById(RecordDo record);

    void deleteRelatedRecordByRecordId(Integer recordId);
}
