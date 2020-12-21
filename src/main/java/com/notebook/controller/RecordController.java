package com.notebook.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.notebook.config.NeedLogin;
import com.notebook.config.storage.FileStorageService;
import com.notebook.domain.RecordDo;
import com.notebook.domain.SwanRequestBody;
import com.notebook.service.*;
import com.notebook.util.ReturnCode;
import com.notebook.util.ReturnResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.time.LocalDateTime;
import java.util.List;

/**
 * <p>
 * 记录信息表 前端控制器
 * </p>
 *
 * @author evan
 * @since 2020-11-08
 */
@Slf4j
@RestController
@RequestMapping("/record")
public class RecordController {
    private final RecordService recordService;
    private final StorageService storageService;
    private final FileStorageService fileStorageService;
    private final PlatformTransactionManager txManager;
    private final AuditService auditService;


    public RecordController(RecordService recordService, StorageService storageService,
                            FileStorageService fileStorageService, ShareRecordService shareRecordService,
                            ShareService shareService, AuditService auditService,
                            PlatformTransactionManager txManager) {
        this.recordService = recordService;
        this.storageService = storageService;
        this.fileStorageService = fileStorageService;
        this.txManager = txManager;
        this.auditService = auditService;
    }

    @NeedLogin
    @GetMapping("/list-all")
    public ReturnResult listShareRecords(SwanRequestBody<?> requestBody,
                                         @RequestParam(defaultValue = "1") Integer tagId,
                                         @RequestParam(defaultValue = "1") Integer recordType) {
        Integer userId = requestBody.getUserId();
        List<RecordDo> result = this.recordService.list(new LambdaQueryWrapper<RecordDo>()
                .eq(RecordDo::getUserId, userId).eq(RecordDo::getTagId, tagId)
                .eq(RecordDo::getRecordType, recordType));
        result.forEach(s -> s.setPicUrl(fileStorageService.generateUrls(s.getPicUrl())));
        return ReturnResult.newInstance().setCode(ReturnCode.SUCCESS).putData("result", result);
    }

    @NeedLogin
    @GetMapping("/list")
    public ReturnResult listRecords(SwanRequestBody<?> requestBody,
                                    @RequestParam(defaultValue = "0") Integer sortType,
                                    @RequestParam(defaultValue = "1") Integer tagId,
                                    @RequestParam(defaultValue = "0") Integer recordType,
                                    @RequestParam(defaultValue = "1") Integer page,
                                    @RequestParam(defaultValue = "10") Integer size) {
        Integer userId = requestBody.getUserId();
        // Page<RecordDo> queryObj = this.recordService.listPagedRecordByUserIdAndTagId(userId, tagId, page, size, sortType);
        Page<RecordDo> queryObj = this.recordService.newListPagedRecord(userId, recordType, tagId, page, size, sortType);
        List<RecordDo> result = queryObj.getRecords();
        result.forEach(s -> s.setPicUrl(fileStorageService.generateUrls(s.getPicUrl())));
        return ReturnResult.newInstance().setCode(ReturnCode.SUCCESS)
                .putData("recordList", result)
                .putData("total", queryObj.getTotal());
    }

    @NeedLogin
    @PostMapping("/add")
    public ReturnResult addRecords(@RequestBody SwanRequestBody<RecordDo> requestBody,
                                   HttpServletResponse resp) {
        RecordDo recordDo = requestBody.getData();
        recordDo.setUserId(requestBody.getUserId());
        recordDo.setAddTime(LocalDateTime.now());
        recordDo.setModifyTime(LocalDateTime.now());
        boolean result = recordService.save(recordDo);
        // if (result) {
        //     // 当数据库操作成功时，删除临时区的文件信息
        //     storageService.deleteTempCache(recordDo.getPicUrl().toArray(String[]::new));
        // }
        auditService.auditRecord(recordDo);
        return ReturnResult.newInstance().setCode(result ? ReturnCode.SUCCESS : ReturnCode.INTERNAL_ERROR)
                .putData("recordId", recordDo.getRecordId());
    }

    @NeedLogin
    @PostMapping("/delete")
    public ReturnResult deleteRecord(@RequestBody SwanRequestBody<Integer> requestBody,
                                     HttpServletResponse response) {
        Integer recordId = requestBody.getData();
        if (recordId == null) {
            return ReturnResult.newInstance().setCode(ReturnCode.PARAM_NOT_COMPLETE);
        }
        DefaultTransactionDefinition def = new DefaultTransactionDefinition();
        def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
        TransactionStatus status = txManager.getTransaction(def);
        boolean result;
        try {
            // 查询记录
            RecordDo record = recordService.getById(recordId);
            if (record == null) {
                return ReturnResult.newInstance().setCode(ReturnCode.PARAM_IS_INVALID);
            }
            List<String> recordPicKeys = record.getPicUrl();
            result = recordService.removeById(recordId);
            if (result) {
                if (!recordPicKeys.isEmpty()) {
                    boolean storageResult = storageService.deleteByKeyBatch(recordPicKeys);
                    if (!storageResult) {
                        log.error("Cannot delete key {}, while record has been removed", recordPicKeys);
                    }
                }
                // Set<Integer> emptyShareIds = this.shareRecordService.deleteShareRecord(recordId);
                // 删除没有记录share
                // shareService.removeByIds(emptyShareIds);
            }
        } catch (Exception e) {
            // 出错，回滚事务
            log.error(e.getMessage());
            txManager.rollback(status);
            return ReturnResult.newInstance().setCode(ReturnCode.INTERNAL_ERROR);
        }
        txManager.commit(status);
        return ReturnResult.newInstance().setCode(result ? ReturnCode.SUCCESS : ReturnCode.INTERNAL_ERROR);
    }

    @NeedLogin
    @PostMapping("/update")
    public ReturnResult updateRecord(@RequestBody SwanRequestBody<RecordDo> requestBody,
                                     HttpServletResponse resp) {
        RecordDo record = requestBody.getData();
        if (record == null || record.getRecordId() == null) {
            // 若更新的实体或实体ID为空则返回参数完整错误
            return ReturnResult.newInstance().setCode(ReturnCode.PARAM_NOT_COMPLETE);
        }
        // 更新记录时认为给定的图片URL已经上传成功，和添加记录的逻辑一致，不再校验图片存储的合法性
        record.setModifyTime(LocalDateTime.now());
        boolean result = recordService.updateEntityById(record);
        // if (result) {
        //     // 当数据库操作成功时，删除临时区的文件信息
        //     storageService.deleteTempCache(record.getPicUrl().toArray(String[]::new));
        // }
        auditService.auditRecord(record);
        return ReturnResult.newInstance().setCode(result ? ReturnCode.SUCCESS : ReturnCode.INTERNAL_ERROR);
    }

    @NeedLogin
    @PostMapping("/count")
    public ReturnResult countRecord(@RequestBody SwanRequestBody<?> requestBody) {
        return ReturnResult.newInstance().setCode(ReturnCode.SUCCESS)
                .putData("count", recordService.count(new LambdaQueryWrapper<RecordDo>()
                        .eq(RecordDo::getUserId, requestBody.getUserId())));
    }
}

