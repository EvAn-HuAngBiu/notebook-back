package com.notebook.controller;


import com.notebook.config.CheckLogin;
import com.notebook.config.NeedLogin;
import com.notebook.dao.cache.CachedLikeDao;
import com.notebook.domain.SwanRequestBody;
import com.notebook.util.ReturnCode;
import com.notebook.util.ReturnResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.Collections;
import java.util.List;

/**
 * <p>
 * 点赞表 前端控制器
 * </p>
 *
 * @author evan
 * @since 2020-11-14
 */
@Slf4j
@RestController
@RequestMapping("/like")
public class LikeController {
    private final CachedLikeDao cachedLikeDao;

    public LikeController(CachedLikeDao cachedLikeDao) {
        this.cachedLikeDao = cachedLikeDao;
    }

    @CheckLogin
    @PostMapping("/check")
    public ReturnResult checkWhetherUserLike(@RequestBody SwanRequestBody<List<Integer>> requestBody,
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
                    .putData("checked", new boolean[shareId.size()]);
        }
        return ReturnResult.newInstance().setCode(ReturnCode.SUCCESS)
                .putData("checked", cachedLikeDao.checkIsLikeBatch(requestBody.getUserId(), shareId));
    }

    @NeedLogin
    @PostMapping("/like")
    public ReturnResult likeShare(@RequestBody SwanRequestBody<Integer> requestBody,
                                             HttpServletResponse response) {
        Integer shareId = requestBody.getData();
        if (shareId == null) {
            return ReturnResult.newInstance().setCode(ReturnCode.PARAM_NOT_COMPLETE);
        }
        cachedLikeDao.likeShare(requestBody.getUserId(), shareId);
        return ReturnResult.newInstance().setCode(ReturnCode.SUCCESS);
    }

    @NeedLogin
    @PostMapping("/dislike")
    public ReturnResult dislikeShare(@RequestBody SwanRequestBody<Integer> requestBody,
                                  HttpServletResponse response) {
        Integer shareId = requestBody.getData();
        if (shareId == null) {
            return ReturnResult.newInstance().setCode(ReturnCode.PARAM_NOT_COMPLETE);
        }
        cachedLikeDao.dislikeShare(requestBody.getUserId(), shareId);
        return ReturnResult.newInstance().setCode(ReturnCode.SUCCESS);
    }

    @NeedLogin
    @GetMapping("/total-like")
    public ReturnResult getTotalLike(SwanRequestBody<?> requestBody) {
        return ReturnResult.newInstance().setCode(ReturnCode.SUCCESS)
                .putData("total", this.cachedLikeDao.getTotalLike(requestBody.getUserId()))
                .putData("new", this.cachedLikeDao.getNewCount(requestBody.getUserId()));
    }
}

