package com.notebook.domain.vo;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.notebook.domain.CommentDo;
import com.notebook.domain.RecordDo;
import com.notebook.domain.ShareDo;
import com.notebook.domain.UserDo;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * Project: notebook
 * File: ShareBriefVo
 *
 * @author evan
 * @date 2020/11/9
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ShareBriefVo implements Serializable {
    private ShareDo shareDo;

    private UserDo userDo;

    private List<RecordDo> recordDo;

    private Boolean like = false;

    private Boolean collect = false;

    private List<ShareCommentVo> comments;

    private int totalComments;
}
