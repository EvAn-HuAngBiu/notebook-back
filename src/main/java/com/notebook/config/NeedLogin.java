package com.notebook.config;

import java.lang.annotation.*;

/**
 * 标记接口需要进行登录状态校验
 *
 * 注意实现本接口需要在参数中提供sessionKey和userId参数
 * @author evan
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface NeedLogin {
}
