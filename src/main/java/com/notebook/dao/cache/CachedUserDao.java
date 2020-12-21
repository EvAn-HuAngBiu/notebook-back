package com.notebook.dao.cache;

import com.notebook.domain.vo.UserVo;

/**
 * @author evan
 */
public interface CachedUserDao {
    void addCachedUserInfo(UserVo info);

    UserVo getCachedUserInfoByUserId(Integer userId);
}
