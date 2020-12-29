package com.notebook.dao.mapper;

import com.notebook.domain.vo.ShareCommentVo;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentVoMapper {
    List<ShareCommentVo> selectBriefComments(@Param("shareId") Integer shareId);

    int countShareComments(@Param("shareId") Integer shareId);

    List<ShareCommentVo> selectDetailComments(@Param("shareId") Integer shareId, @Param("sortType") String sort);

    List<ShareCommentVo> selectSubCommentsByShareId(@Param("shareId") Integer shareId);

    int selectSubCommentsCount(@Param("commentId") Integer commentId);
}
