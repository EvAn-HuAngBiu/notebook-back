package com.notebook.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.notebook.domain.CommentDo;
import com.baomidou.mybatisplus.extension.service.IService;
import com.notebook.domain.vo.ShareCommentListVo;
import com.notebook.domain.vo.ShareCommentVo;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author evan
 * @since 2020-12-21
 */
public interface CommentService extends IService<CommentDo> {
    List<CommentDo> getCommentByShareId(Integer shareId);

    Page<CommentDo> getBriefPagedResultByShareId(Integer shareId);

    List<ShareCommentVo> getBriefCommentInfo(Integer shareId);

    List<ShareCommentListVo> getDetailCommentInfo(Integer shareId, Integer sortType);
}
