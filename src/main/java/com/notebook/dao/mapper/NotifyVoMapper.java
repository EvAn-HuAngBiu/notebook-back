package com.notebook.dao.mapper;

import com.notebook.domain.dto.NotifyBriefDto;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotifyVoMapper {
    Integer checkHasUnread(@Param("userId") Integer userId);

    List<NotifyBriefDto> selectNotifyByUserId(@Param("userId") Integer userId,
                                              @Param("pageStart") Integer pageStart,
                                              @Param("pageSize") Integer pageSize);
}
