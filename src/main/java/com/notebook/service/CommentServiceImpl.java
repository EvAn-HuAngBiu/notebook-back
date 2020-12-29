package com.notebook.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.notebook.dao.mapper.CommentMapper;
import com.notebook.dao.mapper.CommentVoMapper;
import com.notebook.domain.CommentDo;
import com.notebook.domain.vo.ShareCommentListVo;
import com.notebook.domain.vo.ShareCommentVo;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author evan
 * @since 2020-12-21
 */
@Service
public class CommentServiceImpl extends ServiceImpl<CommentMapper, CommentDo> implements CommentService {
    private final CommentVoMapper commentVoMapper;

    public CommentServiceImpl(CommentVoMapper commentVoMapper) {
        this.commentVoMapper = commentVoMapper;
    }

    @Override
    public List<CommentDo> getCommentByShareId(Integer shareId) {
        return this.list(new LambdaQueryWrapper<CommentDo>().eq(CommentDo::getShareId, shareId));
    }

    @Override
    public Page<CommentDo> getBriefPagedResultByShareId(Integer shareId) {
        Page<CommentDo> page = new Page<>(1, 2, true);
        return this.page(page, new LambdaQueryWrapper<CommentDo>().eq(CommentDo::getShareId, shareId));
    }

    @Override
    public List<ShareCommentVo> getBriefCommentInfo(Integer shareId) {
        return commentVoMapper.selectBriefComments(shareId);
    }

    @Override
    public List<ShareCommentListVo> getDetailCommentInfo(Integer shareId, Integer sortType) {
        // 查询当前分享的所有第一级评论
        List<ShareCommentVo> comments = commentVoMapper.selectDetailComments(shareId,
                sortType == 0 ? "DESC" : "ASC");
        // 查询当前分享的所有第二级评论
        List<ShareCommentVo> subComments = commentVoMapper.selectSubCommentsByShareId(shareId);
        int totalSize = comments.size() + subComments.size();
        // 列出CommentID与用户名之间的映射
        Map<Integer, String> parentMap = new HashMap<>(totalSize);
        // 构建并查集
        Map<Integer, Integer> uf = new HashMap<>(comments.size());
        // 保存结果集
        Map<Integer, ShareCommentListVo> result = new LinkedHashMap<>(comments.size());
        comments.forEach(c -> {
            int commentId = c.getCommentDo().getCommentId();
            parentMap.put(commentId, c.getUserDo().getNickname());
            uf.put(commentId, commentId);
            ShareCommentListVo cur = new ShareCommentListVo(c, new ArrayList<>());
            result.put(commentId, cur);
        });
        // 处理二级评论, 构造并查集
        subComments.forEach(c -> {
            int commentId = c.getCommentDo().getCommentId();
            parentMap.put(commentId, c.getUserDo().getNickname());
            uf.put(commentId, uf.get(c.getCommentDo().getParentCommentId()));
            c.setParentNickname(parentMap.get(c.getCommentDo().getParentCommentId()));
            result.get(uf.get(commentId)).getSubCommentList().add(c);
        });
        return List.copyOf(result.values());
    }
}
