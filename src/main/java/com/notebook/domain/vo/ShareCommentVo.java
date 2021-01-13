package com.notebook.domain.vo;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.notebook.domain.CommentDo;
import com.notebook.domain.UserDo;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * Project: notebook
 * File: ShareBriefCommentVo
 *
 * @author evan
 * @date 2020/12/21
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ShareCommentVo implements Serializable {
    private CommentDo commentDo;

    private UserDo userDo;

    private String parentNickname;
}
