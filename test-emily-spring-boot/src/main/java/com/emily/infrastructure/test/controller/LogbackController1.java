package com.emily.infrastructure.test.controller;

import com.emily.infrastructure.datasource.DataSourceAutoConfiguration;
import com.emily.infrastructure.logback.factory.LoggerFactory;
import org.slf4j.Logger;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @program: spring-parent
 * @description: 日志控制器
 * @author: Emily
 * @create: 2021/05/31
 */
@RestController
@RequestMapping("logback")
public class LogbackController1 {
    private static final Logger logger = LoggerFactory.getLogger(LogbackController1.class);

    @GetMapping("debug1")
    public String debug() {
        org.slf4j.LoggerFactory.getLogger(LogbackController.class).info("---------------------&&&&&&&&&&&&&&1111111111111111111111111");
        org.slf4j.LoggerFactory.getLogger(DataSourceAutoConfiguration.class).info("---------------------&&&&&&&&&&&&&&1111111111111111111111111");
        logger.debug("shuai1 +++++++++++++++++++++++++++++++++++++debug");
        logger.warn("shuai2 +++++++++++++++++++++++++++++++++++++warn");
        logger.info("shuai3 +++++++++++++++++++++++++++++++++++++info");
        logger.error("shuai4 +++++++++++++++++++++++++++++++++++++error");
        logger.trace("shuai5 +++++++++++++++++++++++++++++++++++++trace");
        logger.info("++=====================info==========");
        return "success";
    }
}
