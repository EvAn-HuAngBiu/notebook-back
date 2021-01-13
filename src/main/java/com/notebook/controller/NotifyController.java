package com.notebook.controller;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.notebook.config.NeedLogin;
import com.notebook.domain.NotifyDo;
import com.notebook.domain.SwanRequestBody;
import com.notebook.domain.dto.NotifyBriefDto;
import com.notebook.service.NotifyService;
import com.notebook.util.ReturnCode;
import com.notebook.util.ReturnResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author evan
 * @since 2020-12-25
 */
@RestController
@RequestMapping("/notify")
public class NotifyController {
    private final NotifyService notifyService;

    public NotifyController(NotifyService notifyService) {
        this.notifyService = notifyService;
    }

    @NeedLogin
    @GetMapping("/list")
    public ReturnResult listNotify(SwanRequestBody<?> requestBody,
                                   @RequestParam(defaultValue = "1") Integer page,
                                   @RequestParam(defaultValue = "10") Integer size) {
        Integer userId = requestBody.getUserId();
        return ReturnResult.newInstance().setCode(ReturnCode.SUCCESS)
                .putData("notifies", notifyService.selectNotifyByUserId(userId, page, size));
    }

    @NeedLogin
    @GetMapping("/check")
    public ReturnResult checkNotify(SwanRequestBody<?> requestBody) {
        Integer userId = requestBody.getUserId();
        return ReturnResult.newInstance().setCode(ReturnCode.SUCCESS)
                .putData("checked", notifyService.checkHasNewNotify(userId));
    }

    @NeedLogin
    @PostMapping("/uncheck")
    public ReturnResult uncheckNotify(@RequestBody SwanRequestBody<Integer> requestBody) {
        Integer notifyId = requestBody.getData();
        NotifyDo notify = new NotifyDo();
        notify.setReadType(true);
        boolean result = notifyService.update(notify, new LambdaQueryWrapper<NotifyDo>()
                .eq(NotifyDo::getNotifyId, notifyId));
        return ReturnResult.newInstance().setCode(result ? ReturnCode.SUCCESS : ReturnCode.INTERNAL_ERROR);
    }
}

