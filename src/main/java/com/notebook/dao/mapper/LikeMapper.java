package com.notebook.dao.mapper;

import com.notebook.domain.LikeDo;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 点赞表 Mapper 接口
 * </p>
 *
 * @author evan
 * @since 2020-11-14
 */
public interface LikeMapper extends BaseMapper<LikeDo> {
    List<Map<String, Object>> selectUserLikedCount();

    List<Map<String, Object>> selectUserWhoLikedMe();
}
