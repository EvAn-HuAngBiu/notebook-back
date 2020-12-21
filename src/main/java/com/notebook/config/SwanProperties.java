package com.notebook.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Project: course_mall
 * File: WxProperties
 *
 * @author evan
 * @date 2020/10/26
 */
@Data
@Component
@ConfigurationProperties(prefix = "notebook.swan")
public class SwanProperties {
    private String appId;

    private String appSecret;

    private String appKey;
}
