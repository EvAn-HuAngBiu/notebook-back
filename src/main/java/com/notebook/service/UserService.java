package com.notebook.service;

import com.notebook.domain.UserDo;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author evan
 * @since 2020-11-04
 */
public interface UserService extends IService<UserDo> {
    UserDo getUserByOpenId(String openid);

    boolean saveOrUpdateByDto(UserDo userDO, String sessionKey);
}
