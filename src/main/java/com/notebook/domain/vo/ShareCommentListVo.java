package com.notebook.domain.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

/**
 * Project: notebook
 * File: ShareCommentListVo
 *
 * @author evan
 * @date 2020/12/22
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ShareCommentListVo {
    private ShareCommentVo comment;

    private List<ShareCommentVo> subCommentList;
}
