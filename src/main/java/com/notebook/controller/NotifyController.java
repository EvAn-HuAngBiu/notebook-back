package com.notebook.controller;


import com.notebook.config.NeedLogin;
import com.notebook.domain.SwanRequestBody;
import com.notebook.domain.dto.NotifyBriefDto;
import com.notebook.service.NotifyService;
import com.notebook.util.ReturnCode;
import com.notebook.util.ReturnResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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
}

