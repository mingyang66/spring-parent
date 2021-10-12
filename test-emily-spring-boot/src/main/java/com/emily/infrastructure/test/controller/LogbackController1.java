package com.emily.infrastructure.test.controller;

import com.emily.infrastructure.datasource.DataSourceAutoConfiguration;
import com.emily.infrastructure.logback.factory.LogbackFactory;
import org.slf4j.LoggerFactory;
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

    @GetMapping("debug1")
    public String debug() {
        LoggerFactory.getLogger(LogbackController.class).info("---------------------&&&&&&&&&&&&&&1111111111111111111111111");
        LoggerFactory.getLogger(DataSourceAutoConfiguration.class).info("---------------------&&&&&&&&&&&&&&1111111111111111111111111");
        LogbackFactory.debug(LogbackController1.class, "shuai1 +++++++++++++++++++++++++++++++++++++debug");
        LogbackFactory.warn(LogbackController1.class, "shuai2 +++++++++++++++++++++++++++++++++++++warn");
        LogbackFactory.info(LogbackController1.class, "shuai3 +++++++++++++++++++++++++++++++++++++info");
        LogbackFactory.error(LogbackController1.class, "shuai4 +++++++++++++++++++++++++++++++++++++error");
        LogbackFactory.trace(LogbackController1.class, "shuai5 +++++++++++++++++++++++++++++++++++++trace");
        LoggerFactory.getLogger(LogbackController1.class).info("++=====================info==========");
        return "success";
    }
}
