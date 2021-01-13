package com.notebook.config;

import com.notebook.util.ReturnCode;
import com.notebook.util.ReturnResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Arrays;

/**
 * Project: course_mall
 * File: GlobalExceptionHandler
 *
 * @author evan
 * @date 2020/10/28
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(Exception.class)
    public ReturnResult handleAllException(Exception e) {
        log.error(e.getMessage());
        log.error(Arrays.toString(e.getStackTrace()));
        e.printStackTrace();
        return ReturnResult.newInstance().setCode(ReturnCode.INTERNAL_ERROR);
    }
}
