package com.notebook.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.CollectionType;
import com.notebook.domain.RecordDo;
import com.notebook.domain.dto.AuditPenaltyResult;
import io.netty.util.internal.StringUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
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
    public static final String AUDIT_PREFIX = "notebook:audit";

    /**
     * 保存内容审核结果
     * 格式 hash userId->Result(JSON)
     * */
    public static final String AUDIT_RESULT = AUDIT_PREFIX + "result";

    private final RabbitTemplate rabbitTemplate;
    private final StringRedisTemplate redisTemplate;

    public AuditServiceImpl(RabbitTemplate rabbitTemplate, StringRedisTemplate redisTemplate) {
        this.rabbitTemplate = rabbitTemplate;
        this.redisTemplate = redisTemplate;
    }

    @Override
    public void auditRecord(RecordDo recordDo) {
        submitTextAudit(recordDo.getRecordTitle(), recordDo.getUserId(), recordDo.getRecordId());
        submitTextAudit(recordDo.getRecordText(), recordDo.getUserId(), recordDo.getRecordId());
        recordDo.getPicUrl().forEach(image -> {
            submitImageAudit(image, recordDo.getUserId(), recordDo.getRecordId());
        });
    }

    @Override
    public void submitTextAudit(String text, Integer userId, Integer recordId) {
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
        List<AuditPenaltyResult> list = new ArrayList<>();
        String result = ((String) redisTemplate.opsForHash().get(AUDIT_RESULT, userId.toString()));
        if (StringUtils.isBlank(result)) {
            return List.of();
        }
        ObjectMapper mapper = new ObjectMapper();
        CollectionType collectionType = mapper.getTypeFactory().constructCollectionType(List.class, Map.class);
        try {
            List<Map> resultJsonList = mapper.readValue(result, collectionType);
            resultJsonList.forEach(l -> {
                list.add(new AuditPenaltyResult(userId, ((Integer) l.get("recordId")),
                        ((String) l.get("auditResult"))));
            });
            return list;
        } catch (JsonProcessingException e) {
            log.error(e.getMessage());
            log.error(Arrays.toString(e.getStackTrace()));
            e.printStackTrace();
            return List.of();
        }
    }
}
