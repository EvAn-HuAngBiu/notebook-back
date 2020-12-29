package com.notebook.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Project: notebook
 * File: NotifyBriefDto
 *
 * @author evan
 * @date 2020/12/28
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class NotifyBriefDto {
    private Integer notifyId;

    private Integer notifyUserId;

    private Boolean readType;

    private String commentContent;

    private Integer shareId;

    private LocalDateTime addTime;

    private String recordTitle;

    private Integer recordType;
}
