package com.notebook.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Project: notebook
 * File: SwanBriefCommentDto
 *
 * @author evan
 * @date 2020/12/25
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SwanBriefCommentDto {
    private Integer shareId;

    private String content;
}
