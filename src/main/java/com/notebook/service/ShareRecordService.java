package com.notebook.service;

import com.notebook.domain.ShareRecordDo;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;
import java.util.Set;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author evan
 * @since 2020-11-09
 */
public interface ShareRecordService extends IService<ShareRecordDo> {
    Set<Integer> deleteShareRecord(Integer recordId);
}
