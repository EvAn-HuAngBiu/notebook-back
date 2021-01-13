package com.notebook.service;

import com.notebook.domain.RecordDo;
import com.notebook.domain.ShareDo;
import com.notebook.domain.dto.AuditPenaltyResult;

import java.util.List;

/**
 * @author evan
 */
public interface AuditService {
    void auditRecord(RecordDo recordDo);

    void submitTextAudit(List<String> text, Integer userId, Integer recordId);

    void submitImageAudit(String imageUrl, Integer userId, Integer recordId);

    List<AuditPenaltyResult> fetchAuditResult(Integer userId);
}
