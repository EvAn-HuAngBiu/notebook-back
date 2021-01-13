package com.notebook.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Project: notebook
 * File: AuditPenaltyResult
 *
 * @author evan
 * @date 2020/12/19
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuditPenaltyResult {
    private Integer userId;

    private Integer recordId;

    private String recordTitle;

    private String auditLabel;

    private String auditResult;
}
