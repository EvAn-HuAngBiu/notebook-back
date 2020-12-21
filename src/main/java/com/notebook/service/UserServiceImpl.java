package com.notebook.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.notebook.dao.cache.CachedUserDao;
import com.notebook.domain.UserDo;
import com.notebook.dao.mapper.UserMapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.notebook.domain.vo.UserVo;
import org.springframework.stereotype.Service;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author evan
 * @since 2020-11-04
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, UserDo> implements UserService {
    private final CachedUserDao cachedUserDao;

    public UserServiceImpl(CachedUserDao cachedUserDao) {
        this.cachedUserDao = cachedUserDao;
    }

    @Override
    public UserDo getUserByOpenId(String openid) {
        return this.getOne(new QueryWrapper<UserDo>().eq("openid", openid).last("LIMIT 1"));
    }

    @Override
    public boolean saveOrUpdateByDto(UserDo userDO, String sessionKey) {
        boolean updateDbResult = this.saveOrUpdate(userDO, new QueryWrapper<UserDo>().eq("openid", userDO.getOpenid()));
        if (updateDbResult) {
            Integer userId = userDO.getUserId() == null ? getUserByOpenId(userDO.getOpenid()).getUserId() :
                    userDO.getUserId();
            UserVo userVo = new UserVo(userId, userDO.getOpenid(), sessionKey);
            cachedUserDao.addCachedUserInfo(userVo);
            userDO.setUserId(userId);
        }
        return updateDbResult;
    }
}
