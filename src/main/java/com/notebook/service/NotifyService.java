package com.notebook.service;

import com.notebook.domain.CommentDo;
import com.notebook.domain.NotifyDo;
import com.baomidou.mybatisplus.extension.service.IService;
import com.notebook.domain.dto.NotifyBriefDto;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author evan
 * @since 2020-12-25
 */
public interface NotifyService extends IService<NotifyDo> {
    List<NotifyBriefDto> selectNotifyByUserId(Integer userId, Integer page, Integer size);

    Boolean checkHasNewNotify(Integer userId);

    Boolean saveFromComment(CommentDo commentDo, Integer targetUserId);
}
