package com.notebook.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Project: course_mall
 * File: WxRequestBody
 *
 * @author evan
 * @date 2020/10/28
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SwanRequestBody<T> {
    private Integer userId;
    private String sessionKey;
    private T data;
}
