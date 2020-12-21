package com.notebook.controller;


import com.notebook.config.NeedLogin;
import com.notebook.dao.cache.CachedCollectDao;
import com.notebook.domain.SwanRequestBody;
import com.notebook.domain.vo.ShareBriefVo;
import com.notebook.service.ShareService;
import com.notebook.service.ShareVoService;
import com.notebook.util.ReturnCode;
import com.notebook.util.ReturnResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.Collections;
import java.util.List;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author evan
 * @since 2020-11-16
 */
@Slf4j
@RestController
@RequestMapping("/collect")
@SuppressWarnings("all")
public class CollectController {
    // private final CollectService collectService;
    private final CachedCollectDao cachedCollectDao;
    private final ShareVoService shareVoService;
    private final ShareService shareService;

    public CollectController(CachedCollectDao cachedCollectDao,
                             ShareVoService shareVoService, ShareService shareService) {
        // this.collectService = collectService;
        this.cachedCollectDao = cachedCollectDao;
        this.shareVoService = shareVoService;
        this.shareService = shareService;
    }

    @NeedLogin
    @GetMapping("/list")
    public ReturnResult listCollect(SwanRequestBody<?> requestBody,
                                    @RequestParam(defaultValue = "1") Integer page,
                                    @RequestParam(defaultValue = "10") Integer size) {
        Integer userId = requestBody.getUserId();
        // Page<CollectDo> iPage = collectService.listPagedCollections(userId, page, size);
        // List<Integer> allLikes = iPage.getRecords().stream().
        //         map(CollectDo::getShareId).collect(Collectors.toList());
        List<Integer> allLikes = cachedCollectDao.getPagedUserCollects(userId, page, size);
        if (allLikes.isEmpty()) {
            return ReturnResult.newInstance().setCode(ReturnCode.SUCCESS)
                    .putData("shareList", Collections.EMPTY_LIST);
        }
        List<ShareBriefVo> result = shareVoService.getShareBriefByBatchIds(allLikes);
        Collections.reverse(result);
        shareVoService.handleShareLikeAndCollect(result, userId);
        return ReturnResult.newInstance().setCode(ReturnCode.SUCCESS)
                .putData("shareList", result);
    }

    @NeedLogin
    @GetMapping("/count")
    public ReturnResult countCollect(SwanRequestBody<?> requestBody) {
        Integer userId = requestBody.getUserId();
        // int counts = collectService.count(new LambdaQueryWrapper<CollectDo>()
        //         .eq(CollectDo::getUserId, userId));
        long counts = cachedCollectDao.getUserTotalCollects(userId);
        return ReturnResult.newInstance().setCode(ReturnCode.SUCCESS)
                .putData("collectCount", counts);
    }

    @NeedLogin
    @PostMapping("/collect")
    public ReturnResult collectShare(@RequestBody SwanRequestBody<Integer> requestBody,
                                     HttpServletResponse response) {
        Integer userId = requestBody.getUserId();
        Integer shareId = requestBody.getData();
        if (shareId == null || shareId == 0) {
            return ReturnResult.newInstance().setCode(ReturnCode.PARAM_NOT_COMPLETE);
        }
        // CollectDo collectDo = new CollectDo();
        // collectDo.setUserId(userId);
        // collectDo.setShareId(shareId);
        // boolean result = this.collectService.save(collectDo);
        // if (!this.shareService.increaseShareCollect(shareId)) {
        //     log.warn("Cannot increase collect count for share id: {}, domain is: {}", shareId, collectDo);
        // }
        cachedCollectDao.collectShare(userId, shareId);
        return ReturnResult.newInstance().setCode(ReturnCode.SUCCESS);
    }

    @NeedLogin
    @PostMapping("/cancel")
    public ReturnResult cancelShare(@RequestBody SwanRequestBody<Integer> requestBody,
                                    HttpServletResponse response) {
        Integer userId = requestBody.getUserId();
        Integer shareId = requestBody.getData();
        if (shareId == null || shareId == 0) {
            return ReturnResult.newInstance().setCode(ReturnCode.PARAM_NOT_COMPLETE);
        }
        // boolean result = this.collectService.remove(new LambdaQueryWrapper<CollectDo>()
        //         .eq(CollectDo::getUserId, userId).eq(CollectDo::getShareId, shareId));
        // if (!this.shareService.decreaseShareCollect(shareId)) {
        //     log.warn("Cannot decrease collect count for share id: {}", shareId);
        // }
        cachedCollectDao.cancelCollectShare(userId, shareId);
        return ReturnResult.newInstance().setCode(ReturnCode.SUCCESS);
    }

    @NeedLogin
    @PostMapping("/check")
    public ReturnResult checkWhetherUserCollect(@RequestBody SwanRequestBody<List<Integer>> requestBody,
                                                HttpServletResponse response) {
        List<Integer> shareId = requestBody.getData();
        if (shareId == null) {
            return ReturnResult.newInstance().setCode(ReturnCode.PARAM_NOT_COMPLETE);
        } else if (shareId.isEmpty()) {
            return ReturnResult.newInstance().setCode(ReturnCode.SUCCESS)
                    .putData("checked", Collections.emptyList());
        }
        Integer userId = requestBody.getUserId();
        if (userId == null) {
            return ReturnResult.newInstance().setCode(ReturnCode.SUCCESS)
                    .putData("checked", List.of(new boolean[shareId.size()]));
        }
        return ReturnResult.newInstance().setCode(ReturnCode.SUCCESS)
                .putData("checked", this.cachedCollectDao.checkIsCollectBatch(userId, shareId));
        // return ReturnResult.newInstance().setCode(ReturnCode.SUCCESS)
        //         .putData("checked", this.collectService.checkWhetherUserCollectBatchByShareId(userId, shareId));
    }
}

