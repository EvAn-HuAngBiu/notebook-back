package com.notebook;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * Project: notebook
 * File: NotebookApplication
 *
 * @author evan
 * @date 2020/11/4
 */
@SpringBootApplication
@MapperScan("com.notebook.dao.mapper")
@EnableCaching
@EnableTransactionManagement
@EnableScheduling
public class NotebookApplication {
    public static void main(String[] args) {
        SpringApplication.run(NotebookApplication.class, args);
    }
}
