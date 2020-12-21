package com.notebook.domain.vo;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.notebook.domain.ShareDo;
import com.notebook.domain.UserDo;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Project: notebook
 * File: ShareUserInfoVo
 *
 * @author evan
 * @date 2020/11/11
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ShareUserInfoVo {
    private ShareDo shareDo;

    private UserDo userDo;
}
