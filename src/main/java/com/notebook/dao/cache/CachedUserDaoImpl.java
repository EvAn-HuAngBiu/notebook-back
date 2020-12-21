package com.notebook.dao.cache;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.notebook.domain.vo.UserVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;

/**
 * Project: notebook
 * File: CachedUserDaoImpl
 *
 * @author evan
 * @date 2020/11/6
 */
@Slf4j
@Repository
public class CachedUserDaoImpl implements CachedUserDao {
    public static final String REDIS_USER_INFO_KEY_FLAG = "user_info";

    private final StringRedisTemplate template;

    public CachedUserDaoImpl(StringRedisTemplate template) {
        this.template = template;
    }

    @Override
    public void addCachedUserInfo(UserVo info) {
        ObjectMapper mapper = new ObjectMapper();
        ObjectWriter writer = mapper.writer();
        try {
            String deserializeValue = writer.writeValueAsString(info);
            template.opsForHash().put(REDIS_USER_INFO_KEY_FLAG, info.getUserId().toString(), deserializeValue);
        } catch (JsonProcessingException e) {
            log.error("Cannot deserialize object {}", info);
        }
    }

    @Override
    public UserVo getCachedUserInfoByUserId(Integer userId) {
        String userInfoStr = ((String) template.opsForHash().get(REDIS_USER_INFO_KEY_FLAG, userId.toString()));
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.readValue(userInfoStr, UserVo.class);
        } catch (JsonProcessingException e) {
            log.error("Cannot parse json value {}", userInfoStr);
            return null;
        }
    }
}
