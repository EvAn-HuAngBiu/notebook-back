package com.notebook.controller;


import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.notebook.config.NeedLogin;
import com.notebook.domain.SwanRequestBody;
import com.notebook.domain.UserDo;
import com.notebook.domain.dto.SwanJscode2SessionDto;
import com.notebook.domain.dto.SwanLoginDto;
import com.notebook.domain.dto.SwanUserDto;
import com.notebook.service.UserService;
import com.notebook.swan.SwanRequestService;
import com.notebook.util.IpUtil;
import com.notebook.util.ReturnCode;
import com.notebook.util.ReturnResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.net.UnknownHostException;
import java.time.LocalDateTime;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author evan
 * @since 2020-11-04
 */
@Slf4j
@RestController
@RequestMapping("/auth")
public class AuthController {
    private final UserService userService;
    private final SwanRequestService swanRequestService;

    public AuthController(UserService userService, SwanRequestService swanRequestService) {
        this.userService = userService;
        this.swanRequestService = swanRequestService;
    }

    @PostMapping("/login")
    public ReturnResult loginSystem(@RequestBody SwanLoginDto swanLoginDto, HttpServletRequest request,
                                    HttpServletResponse response) throws UnknownHostException {
        String code = swanLoginDto.getCode();
        SwanUserDto swanUserDto = swanLoginDto.getBdUserInfo();
        if (code == null || swanUserDto == null) {
            return ReturnResult.newInstance().setCode(ReturnCode.PARAM_NOT_COMPLETE);
        }
        String sessionKey;
        String openId;
        try {
            SwanJscode2SessionDto swanJscode2SessionDto = swanRequestService.jscode2SessionKey(code);
            sessionKey = swanJscode2SessionDto.getSessionKey();
            openId = swanJscode2SessionDto.getOpenid();
        } catch (Exception e) {
            log.error("获取SessionKey和OpenID失败，错误: {}", e.getMessage());
            return ReturnResult.newInstance().setCode(ReturnCode.INTERNAL_ERROR);
        }

        if (sessionKey == null || openId == null) {
            return ReturnResult.newInstance().setCode(ReturnCode.FAILED)
                    .putData("errMsg", "百度服务器认证失败");
        }

        UserDo userDO = new UserDo();
        userDO.setNickname(swanUserDto.getNickName());
        userDO.setAvatar(swanUserDto.getAvatarUrl());
        userDO.setGender(swanUserDto.getGender());
        userDO.setLastLoginTime(LocalDateTime.now());
        userDO.setLastLoginIp(IpUtil.getIpAddress(request));
        userDO.setOpenid(openId);
        boolean dbResult = this.userService.saveOrUpdateByDto(userDO, sessionKey);
        if (!dbResult) {
            // 数据库写入失败同时意味着缓存内没有用户登录信息，此时应返回失败
            log.warn("下列用户写入数据库失败 {}", userDO);
            return ReturnResult.newInstance().setCode(ReturnCode.INTERNAL_ERROR);
        }
        String token = JWT.create().withAudience(userDO.getUserId().toString())
                .sign(Algorithm.HMAC256(sessionKey));
        return ReturnResult.newInstance().setCode(ReturnCode.SUCCESS)
                .putData("token", token)
                .putData("userId", userDO.getUserId());
    }

    @NeedLogin
    @GetMapping("/detail")
    public ReturnResult getUserDetailInfo(SwanRequestBody<?> requestBody, HttpServletResponse response) {
        return ReturnResult.newInstance().setCode(ReturnCode.SUCCESS)
                .putData("user", this.userService.getById(requestBody.getUserId()));
    }
}

