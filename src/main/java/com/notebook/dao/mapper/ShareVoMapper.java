package com.notebook.dao.mapper;

import com.notebook.domain.RecordDo;
import com.notebook.domain.vo.ShareUserInfoVo;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Project: notebook
 * File: ShareVoMapper
 *
 * @author evan
 * @date 2020/11/11
 */
@Repository
public interface ShareVoMapper {
    List<ShareUserInfoVo> selectShareAndUserInfo();

    ShareUserInfoVo selectShareAndUserInfoById(@Param("shareId") Integer shareId);

    List<ShareUserInfoVo> selectShareAndUserInfoOrderByHot(@Param("tagId") Integer tagId,
                                                           @Param("pageStart") Integer pageStart,
                                                           @Param("pageSize") Integer pageSize);

    List<ShareUserInfoVo> selectShareAndUserInfoByUser(@Param("userId") Integer userId,
                                                       @Param("pageStart") Integer pageStart,
                                                       @Param("pageSize") Integer pageSize);

    List<ShareUserInfoVo> selectShareAndUserInfoOrderByNew(@Param("tagId") Integer tagId,
                                                           @Param("pageStart") Integer pageStart,
                                                           @Param("pageSize") Integer pageSize);

    ShareUserInfoVo selectShareAndUserInfoByShareId(@Param("shareId") Integer shareId);

    List<RecordDo> selectBriefRecordByShareId(@Param("shareId") Integer shareId);

    List<RecordDo> selectDetailRecordByShareId(@Param("shareId") Integer shareId);

    RecordDo selectOneRecordFromShare(@Param("shareId") Integer shareId);
}
