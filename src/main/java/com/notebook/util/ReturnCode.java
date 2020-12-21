package com.notebook.util;

/**
 * Define code and message in return text
 * Note that it cannot set response status, you need to use
 * {@link javax.servlet.http.HttpServletResponse#setStatus(int)} to set response status manually
 *
 *
 * @author evan
 * @date 2020/10/23
 * @version 1.0.0
 */
public enum ReturnCode {
    /*
     * General error：-1
     * Request successfully：200
     * Unauthorized request：401
     * Request forbidden：403
     * Page not found：404
     * Server inner error：500
     * */
    FAILED(-1, "错误", 200),
    SUCCESS(200, "成功", 200),
    REQUEST_ERROR(400, "请求错误", 400),
    PARAM_IS_INVALID(4001, "参数无效", 400),
    PARAM_IS_BLANK(4002, "参数为空", 400),
    PARAM_TYPE_BIND_ERROR(4003, "参数类型错误", 400),
    PARAM_NOT_COMPLETE(4004, "参数缺失", 400),
    UNAUTHORIZED(401, "未授权", 401),
    USER_NOT_LOGGED_ID(4011, "用户未登录", 401),
    FORBIDDEN(403, "禁止访问", 403),
    USER_LOGIN_ERROR(4031, "用户登录失败:账号不存在或密码错误", 403),
    USER_ACCOUNT_FORBIDDEN(4032, "账户被禁用", 403),
    PAGE_NOT_FOUND(404, "页面未找到", 404),
    INTERNAL_ERROR(500, "内部错误", 500);

    public Integer code;
    public String message;
    public Integer responseCode;

    ReturnCode(Integer code, String message, Integer responseCode) {
        this.code = code;
        this.message = message;
        this.responseCode = responseCode;
    }
}
