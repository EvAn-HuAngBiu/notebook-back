package com.notebook.domain.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Project: notebook
 * File: CachedUserInfo
 *
 * @author evan
 * @date 2020/11/6
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserVo {
    private Integer userId;
    private String openId;
    private String sessionKey;
}
