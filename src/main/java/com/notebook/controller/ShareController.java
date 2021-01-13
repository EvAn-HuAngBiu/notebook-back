package com.notebook.controller;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.notebook.config.CheckLogin;
import com.notebook.config.NeedLogin;
import com.notebook.dao.cache.CachedCollectDao;
import com.notebook.dao.cache.CachedLikeDao;
import com.notebook.domain.CollectDo;
import com.notebook.domain.ShareDo;
import com.notebook.domain.ShareRecordDo;
import com.notebook.domain.SwanRequestBody;
import com.notebook.domain.dto.SwanAddShareDto;
import com.notebook.domain.dto.SwanUpdateShareDto;
import com.notebook.domain.vo.ShareBriefVo;
import com.notebook.service.CollectService;
import com.notebook.service.ShareRecordService;
import com.notebook.service.ShareService;
import com.notebook.service.ShareVoService;
import com.notebook.util.ReturnCode;
import com.notebook.util.ReturnResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author evan
 * @since 2020-11-09
 */
@Slf4j
@RestController
@RequestMapping("/share")
public class ShareController {
    private final ShareService shareService;
    private final ShareRecordService shareRecordService;
    private final ShareVoService shareVoService;
    private final PlatformTransactionManager txManager;
    private final CachedLikeDao cachedLikeDao;
    private final CollectService collectService;
    private final CachedCollectDao cachedCollectDao;

    public ShareController(ShareService shareService, ShareRecordService shareRecordService,
                           ShareVoService shareVoService, PlatformTransactionManager txManager,
                           CachedLikeDao cachedLikeDao, CollectService collectService,
                           CachedCollectDao cachedCollectDao) {
        this.shareService = shareService;
        this.shareRecordService = shareRecordService;
        this.shareVoService = shareVoService;
        this.txManager = txManager;
        this.cachedLikeDao = cachedLikeDao;
        this.cachedCollectDao = cachedCollectDao;
        this.collectService = collectService;
    }

    @CheckLogin
    @GetMapping("/new")
    public ReturnResult listNewRecords(SwanRequestBody<?> requestBody,
                                       @RequestParam Integer tagId,
                                       @RequestParam(name = "page", defaultValue = "1") Integer page,
                                       @RequestParam(name = "size", defaultValue = "10") Integer size) {
        List<ShareBriefVo> shareBrief = shareVoService.getShareBriefVoOrderByNew(tagId, page, size);
        System.out.println(shareBrief);
        Integer userId = requestBody.getUserId();
        if (userId != null) {
            shareVoService.handleShareLikeAndCollect(shareBrief, userId);
        }
        return ReturnResult.newInstance().setCode(ReturnCode.SUCCESS)
                .putData("shareList", shareBrief);
    }

    @CheckLogin
    @GetMapping("/hot")
    public ReturnResult listHotRecords(SwanRequestBody<?> requestBody,
                                       @RequestParam Integer tagId,
                                       @RequestParam(name = "page", defaultValue = "1") Integer page,
                                       @RequestParam(name = "size", defaultValue = "10") Integer size) {
        List<ShareBriefVo> shareBrief = shareVoService.getShareBriefVoOrderByHot(tagId, page, size);
        Integer userId = requestBody.getUserId();
        if (userId != null) {
            shareVoService.handleShareLikeAndCollect(shareBrief, userId);
        }
        return ReturnResult.newInstance().setCode(ReturnCode.SUCCESS)
                .putData("shareList", shareBrief);
    }

    @GetMapping("/detail")
    public ReturnResult listDetail(@RequestParam Integer shareId) {
        return ReturnResult.newInstance().setCode(ReturnCode.SUCCESS)
                .putData("detail", shareVoService.getDetailRecord(shareId));
    }

    @CheckLogin
    @GetMapping("/specify")
    public ReturnResult getSpecifyShare(SwanRequestBody<?> requestBody, @RequestParam Integer shareId) {
        ShareBriefVo shareBrief = shareVoService.getSpecifyShare(shareId);
        Integer userId  = requestBody.getUserId();
        if (userId != null) {
            shareVoService.handleShareLikeAndCollect(List.of(shareBrief), userId);
        }
        return ReturnResult.newInstance().setCode(ReturnCode.SUCCESS)
                .putData("share", shareBrief);
    }

    @NeedLogin
    @PostMapping("/add")
    public ReturnResult addShare(@RequestBody SwanRequestBody<SwanAddShareDto> requestBody) {
        Integer userId = requestBody.getUserId();
        SwanAddShareDto swanAddShareDto = requestBody.getData();
        if (swanAddShareDto == null) {
            return ReturnResult.newInstance().setCode(ReturnCode.PARAM_IS_INVALID);
        }
        Integer tagId = requestBody.getData().getTagId();
        List<Integer> recordIds = requestBody.getData().getRecordIds();
        if (tagId == null || recordIds == null || recordIds.isEmpty()) {
            return ReturnResult.newInstance().setCode(ReturnCode.PARAM_IS_INVALID);
        }

        // 声明事务
        DefaultTransactionDefinition def = new DefaultTransactionDefinition();
        def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
        TransactionStatus status = txManager.getTransaction(def);

        final Integer shareId;
        try {
            // 添加一条分享
            ShareDo shareDo = new ShareDo();
            shareDo.setUserId(userId);
            shareDo.setTagId(tagId);
            shareDo.setLikeCnt(0);
            shareDo.setCollectCnt(0);
            shareDo.setAddTime(LocalDateTime.now());
            shareDo.setModifyTime(LocalDateTime.now());
            boolean shareAddResult = this.shareService.save(shareDo);
            if (!shareAddResult) {
                throw new RuntimeException("Cannot insert share info, original data is " + shareDo);
            }

            // 对该条分享添加物品记录
            shareId = shareDo.getShareId();
            List<ShareRecordDo> shareRecords = recordIds.stream()
                    .map(recordId -> new ShareRecordDo(shareId, recordId))
                    .collect(Collectors.toList());
            boolean shareRecordAddResult = this.shareRecordService.saveBatch(shareRecords);
            if (!shareRecordAddResult) {
                throw new RuntimeException("Cannot insert share record info, original data is " + shareRecords);
            }
        } catch (Exception e) {
            // 出错，回滚事务
            log.error(e.getMessage());
            txManager.rollback(status);
            return ReturnResult.newInstance().setCode(ReturnCode.INTERNAL_ERROR);
        }
        // 提交事务
        txManager.commit(status);
        return ReturnResult.newInstance().setCode(ReturnCode.SUCCESS)
                .putData("shareId", shareId);
    }

    @NeedLogin
    @PostMapping("/delete")
    public ReturnResult deleteShare(@RequestBody SwanRequestBody<Integer> requestBody) {
        Integer userId = requestBody.getUserId();
        Integer shareId = requestBody.getData();
        if (shareId == null || !this.shareService.getById(shareId).getUserId().equals(userId)) {
            return ReturnResult.newInstance().setCode(ReturnCode.PARAM_IS_INVALID);
        }

        // 声明事务
        DefaultTransactionDefinition def = new DefaultTransactionDefinition();
        def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
        TransactionStatus status = txManager.getTransaction(def);

        try {
            boolean deleteShareResult = this.shareService.removeById(shareId);
            if (!deleteShareResult) {
                throw new RuntimeException("Cannot delete share result for shareId " + shareId);
            }
            boolean deleteShareRecordResult = this.shareRecordService.remove(
                    new QueryWrapper<ShareRecordDo>().eq("share_id", shareId));
            if (!deleteShareRecordResult) {
                throw new RuntimeException("Cannot delete share record result for shareId " + shareId);
            }
            this.collectService.remove(
                    new LambdaQueryWrapper<CollectDo>().eq(CollectDo::getShareId, shareId)
            );
        } catch (Exception e) {
            txManager.rollback(status);
            log.error(e.getMessage());
            return ReturnResult.newInstance().setCode(ReturnCode.INTERNAL_ERROR);
        }
        txManager.commit(status);
        return ReturnResult.newInstance().setCode(ReturnCode.SUCCESS);
    }

    @NeedLogin
    @PostMapping("/update")
    public ReturnResult updateShare(@RequestBody SwanRequestBody<SwanUpdateShareDto> requestBody) {
        SwanUpdateShareDto swanUpdateShareDto = requestBody.getData();
        if (swanUpdateShareDto == null) {
            return ReturnResult.newInstance().setCode(ReturnCode.PARAM_IS_INVALID);
        }
        Integer shareId = requestBody.getData().getShareId();
        List<Integer> recordIds = requestBody.getData().getRecordIds();
        if (shareId == null || recordIds == null || recordIds.isEmpty()) {
            return ReturnResult.newInstance().setCode(ReturnCode.PARAM_IS_INVALID);
        }

        // 声明事务
        DefaultTransactionDefinition def = new DefaultTransactionDefinition();
        def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
        TransactionStatus status = txManager.getTransaction(def);

        try {
            // 更新记录时间
            ShareDo shareDo = new ShareDo();
            shareDo.setModifyTime(LocalDateTime.now());
            boolean updateTimeResult = this.shareService.update(shareDo,
                    new QueryWrapper<ShareDo>().eq("share_id", shareId));
            if (!updateTimeResult) {
                throw new RuntimeException(String.format(
                        "Cannot update time for shareId %s, domain is %s", shareId, shareDo));
            }

            // 删除旧记录
            boolean deleteOldResult = this.shareRecordService.remove(
                    new QueryWrapper<ShareRecordDo>().eq("share_id", shareId));
            if (!deleteOldResult) {
                throw new RuntimeException("Cannot delete old share record, share id is " + shareId);
            }

            // 写入新记录
            boolean addNewResult = this.shareRecordService.saveBatch(recordIds.stream()
                    .map(recordId -> new ShareRecordDo(shareId, recordId))
                    .collect(Collectors.toList())
            );
            if (!addNewResult) {
                throw new RuntimeException("Cannot add new result, record ids are " + recordIds);
            }
        } catch (Exception e) {
            txManager.rollback(status);
            log.error(e.getMessage());
            return ReturnResult.newInstance().setCode(ReturnCode.INTERNAL_ERROR);
        }
        txManager.commit(status);
        return ReturnResult.newInstance().setCode(ReturnCode.SUCCESS);
    }

    @NeedLogin
    @GetMapping("/count")
    public ReturnResult countShare(SwanRequestBody<?> requestBody) {
        return ReturnResult.newInstance().setCode(ReturnCode.SUCCESS)
                .putData("total", this.shareService.count(new LambdaQueryWrapper<ShareDo>()
                        .eq(ShareDo::getUserId, requestBody.getUserId())));
    }

    @NeedLogin
    @GetMapping("/my")
    public ReturnResult listMyShares(SwanRequestBody<?> requestBody,
                                     @RequestParam(defaultValue = "1") Integer page,
                                     @RequestParam(defaultValue = "10") Integer size) {
        List<ShareBriefVo> shareBrief = shareVoService.getShareBriefVoByUser(requestBody.getUserId(), page, size);
        shareVoService.handleShareLikeAndCollect(shareBrief, requestBody.getUserId());
        return ReturnResult.newInstance().setCode(ReturnCode.SUCCESS)
                .putData("shareList", shareBrief);
    }
}

