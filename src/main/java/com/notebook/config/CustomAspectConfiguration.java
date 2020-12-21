package com.notebook.config;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.notebook.dao.cache.CachedUserDao;
import com.notebook.domain.SwanRequestBody;
import com.notebook.domain.vo.UserVo;
import com.notebook.util.ReturnCode;
import com.notebook.util.ReturnResult;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;
import java.util.Objects;

/**
 * Project: course_mall
 * File: CustomAspectConfiguration
 *
 * @author evan
 * @date 2020/10/27
 */
@Slf4j
@Aspect
@Configuration
public class CustomAspectConfiguration {
    public static final String SESSION_KEY_FLAG = "sessionKey";
    public static final String TOKEN_HEADER_FLAG = "X-NoteBook-Token";
    public static final String USERID_HEADER_FLAG = "X-UserId-Token";
    public static final ReturnResult NOT_LOGIN_RETURN_RESULT =
            ReturnResult.newInstance().setCode(ReturnCode.USER_NOT_LOGGED_ID).isSuccess(false);
    public static final ReturnResult WRONG_LOGIN_RETURN_RESULT =
            ReturnResult.newInstance().setCode(ReturnCode.UNAUTHORIZED).isSuccess(false);

    private final CachedUserDao cachedUserDao;

    public CustomAspectConfiguration(CachedUserDao cachedUserDao) {
        this.cachedUserDao = cachedUserDao;
    }

    @Pointcut("@annotation(com.notebook.config.NeedLogin)")
    public void needLoginAnnotation() {
    }

    @Pointcut("@annotation(com.notebook.config.CheckLogin)")
    public void handleCheckLoginAnnotation() {
    }

    @Around("needLoginAnnotation()")
    public Object doAroundNeedLoginAnnotation(ProceedingJoinPoint joinPoint) throws Throwable {
        HttpServletRequest request = ((ServletRequestAttributes) Objects.requireNonNull(
                RequestContextHolder.getRequestAttributes())).getRequest();
        HttpServletResponse response = ((ServletRequestAttributes) Objects.requireNonNull(
                RequestContextHolder.getRequestAttributes())).getResponse();
        assert response != null;
        // 获取请求头的token
        String token = request.getHeader(TOKEN_HEADER_FLAG);
        String userIdStr = request.getHeader(USERID_HEADER_FLAG);
        if (token == null || StringUtils.isBlank(userIdStr)) {
            return NOT_LOGIN_RETURN_RESULT;
        }
        Integer userId = null;
        try {
            userId = Integer.parseInt(userIdStr);
        } catch (Exception e) {
            return WRONG_LOGIN_RETURN_RESULT;
        }
        String sessionKey;
        try {
            // 解码判断
            Integer savedId = Integer.parseInt(JWT.decode(token).getAudience().get(0));
            if (!savedId.equals(userId)) {
                // 无法解码则返回登录凭据有误
                return WRONG_LOGIN_RETURN_RESULT;
            }
            UserVo userVo = cachedUserDao.getCachedUserInfoByUserId(savedId);
            sessionKey = userVo.getSessionKey();
            JWTVerifier jwtVerifier = JWT.require(Algorithm.HMAC256(sessionKey)).build();
            // 验证token
            jwtVerifier.verify(token);
        } catch (JWTVerificationException e) {
            // 验证失败
            return WRONG_LOGIN_RETURN_RESULT;
        } catch (Exception e) {
            // 系统错误
            log.error(e.getMessage());
            log.error(Arrays.toString(e.getStackTrace()));
            return WRONG_LOGIN_RETURN_RESULT;
        }
        // 读取请求参数
        Object[] args = joinPoint.getArgs();
        SwanRequestBody<?> body = Arrays.stream(args)
                .filter(o -> o instanceof SwanRequestBody)
                .map(o -> (SwanRequestBody<?>) o)
                .findFirst().orElse(null);
        if (body == null) {
            // 不存在需要登录信息的请求头，则直接访问
            return joinPoint.proceed();
        }
        body.setUserId(userId);
        body.setSessionKey(sessionKey);
        return joinPoint.proceed(args);
    }

    @Around("handleCheckLoginAnnotation()")
    public Object doAroundCheckLoginAnnotation(ProceedingJoinPoint joinPoint) throws Throwable {
        MethodSignature signature = ((MethodSignature) joinPoint.getSignature());
        HttpServletRequest request = ((ServletRequestAttributes) Objects.requireNonNull(
                RequestContextHolder.getRequestAttributes())).getRequest();
        String token = request.getHeader(TOKEN_HEADER_FLAG);
        String userIdStr = request.getHeader(USERID_HEADER_FLAG);
        if (StringUtils.isBlank(userIdStr)) {
            return joinPoint.proceed();
        }
        Integer userId;
        try {
            userId = Integer.parseInt(userIdStr);
        } catch (Exception e) {
            return joinPoint.proceed();
        }
        if (token == null || userId == 0) {
            // 无token直接按未登录放行
            return joinPoint.proceed();
        }
        String sessionKey;
        try {
            // 解码判断
            Integer savedId = Integer.parseInt(JWT.decode(token).getAudience().get(0));
            if (!savedId.equals(userId)) {
                // 无法解码则直接按未登录放行
                return joinPoint.proceed();
            }
            UserVo userVo = cachedUserDao.getCachedUserInfoByUserId(savedId);
            sessionKey = userVo.getSessionKey();
            JWTVerifier jwtVerifier = JWT.require(Algorithm.HMAC256(sessionKey)).build();
            // 验证token
            jwtVerifier.verify(token);
        } catch (JWTVerificationException e) {
            // 验证失败
            return joinPoint.proceed();
        } catch (Exception e) {
            // 系统错误
            log.error(e.getMessage());
            log.error(Arrays.toString(e.getStackTrace()));
            return joinPoint.proceed();
        }
        // 读取请求参数
        Object[] args = joinPoint.getArgs();
        SwanRequestBody<?> body = Arrays.stream(args)
                .filter(o -> o instanceof SwanRequestBody)
                .map(o -> (SwanRequestBody<?>) o)
                .findFirst().orElse(null);
        if (body == null) {
            // 不存在需要登录信息的请求头，则直接访问
            return joinPoint.proceed();
        }
        body.setUserId(userId);
        body.setSessionKey(sessionKey);
        return joinPoint.proceed(args);
    }
}
