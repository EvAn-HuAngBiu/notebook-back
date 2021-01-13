package com.notebook.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.notebook.config.storage.FileStorageService;
import com.notebook.dao.mapper.ShareVoMapper;
import com.notebook.domain.RecordDo;
import com.notebook.domain.ShareDo;
import com.notebook.domain.dto.AuditPenaltyResult;
import com.notebook.util.AuditLabel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * Project: notebook
 * File: AuditServiceImp
 *
 * @author evan
 * @date 2020/12/19
 */
@Slf4j
@Service
public class AuditServiceImpl implements AuditService {
    public static final String AUDIT_IMAGE_RESULT = "audit:image";
    public static final String AUDIT_TEXT_RESULT = "audit:text";

    private final RabbitTemplate rabbitTemplate;
    private final StringRedisTemplate redisTemplate;
    private final FileStorageService fileStorageService;
    private final RecordService recordService;
    private final ShareVoMapper shareVoMapper;

    public AuditServiceImpl(RabbitTemplate rabbitTemplate, StringRedisTemplate redisTemplate,
                            FileStorageService fileStorageService, RecordService recordService,
                            ShareVoMapper shareVoMapper) {
        this.rabbitTemplate = rabbitTemplate;
        this.redisTemplate = redisTemplate;
        this.fileStorageService = fileStorageService;
        this.recordService = recordService;
        this.shareVoMapper = shareVoMapper;
    }

    @Async("uploadThreadPool")
    @Override
    public void auditRecord(RecordDo recordDo) {
            submitTextAudit(List.of(recordDo.getRecordTitle(), recordDo.getRecordText()),
                    recordDo.getUserId(), recordDo.getRecordId());
            recordDo.getPicUrl().forEach(image -> submitImageAudit(fileStorageService.generateUrl(image),
                    recordDo.getUserId(), recordDo.getRecordId()));
    }

    @Override
    public void submitTextAudit(List<String> text, Integer userId, Integer recordId) {
        Map<String, Object> queryMap = new HashMap<>(3);
        queryMap.put("payload", text);
        queryMap.put("userId", userId);
        queryMap.put("recordId", recordId);
        rabbitTemplate.convertAndSend("audit", "text", queryMap);
    }

    @Override
    public void submitImageAudit(String imageUrl, Integer userId, Integer recordId) {
        Map<String, Object> queryMap = new HashMap<>(3);
        queryMap.put("payload", imageUrl);
        queryMap.put("userId", userId);
        queryMap.put("recordId", recordId);
        rabbitTemplate.convertAndSend("audit", "image", queryMap);
    }

    @Override
    public List<AuditPenaltyResult> fetchAuditResult(Integer userId) {
        String imageKey =  AUDIT_IMAGE_RESULT + userId;
        String textKey = AUDIT_TEXT_RESULT + userId;
        List<AuditPenaltyResult> result = getAuditResultsByKey(imageKey, userId);
        result.addAll(getAuditResultsByKey(textKey, userId));
        return result;
    }

    private List<AuditPenaltyResult> getAuditResultsByKey(String key, Integer userId) {
        List<AuditPenaltyResult> results = new ArrayList<>();
        HashOperations<String, Object, Object> hashOperations = redisTemplate.opsForHash();
        List<Object> imageHashKeys = List.copyOf(hashOperations.keys(key));
        if (!imageHashKeys.isEmpty()) {
            List<Object> imageHashValues = hashOperations.multiGet(key, imageHashKeys);
            for (int i = 0; i < imageHashKeys.size(); ++i) {
                AuditPenaltyResult auditResult = new AuditPenaltyResult();
                auditResult.setUserId(userId);
                auditResult.setRecordId(Integer.parseInt(((String) imageHashKeys.get(i))));
                auditResult.setAuditLabel(imageHashValues.get(i).toString());
                auditResult.setAuditResult(AuditLabel.getByLabel(auditResult.getAuditLabel()));
                auditResult.setRecordTitle(recordService.getOne(new LambdaQueryWrapper<RecordDo>()
                        .select(RecordDo::getRecordTitle)
                        .eq(RecordDo::getRecordId, auditResult.getRecordId())
                        .last("LIMIT 1")).getRecordTitle());
                results.add(auditResult);
                recordService.deleteRelatedRecordByRecordId(auditResult.getRecordId());
            }
            redisTemplate.delete(key);
        }
        return results;
    }
}
