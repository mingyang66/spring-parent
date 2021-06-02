package com.emily.infrastructure.test.controller;

import com.emily.infrastructure.logback.utils.LoggerUtils;
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
        LoggerUtils.debug(LogbackController1.class, "shuai1 +++++++++++++++++++++++++++++++++++++debug");
        LoggerUtils.warn(LogbackController1.class, "shuai2 +++++++++++++++++++++++++++++++++++++warn");
        LoggerUtils.info(LogbackController1.class, "shuai3 +++++++++++++++++++++++++++++++++++++info");
        LoggerUtils.error(LogbackController1.class, "shuai4 +++++++++++++++++++++++++++++++++++++error");
        LoggerUtils.module(LogbackController1.class, "test1", "tt0", "ni-----------------" + System.currentTimeMillis());
        LoggerUtils.module(LogbackController1.class, "test1", "tt1", "ni-----------------" + System.currentTimeMillis());
        LoggerUtils.module(LogbackController1.class, "test2", "tt2", "ni-----------------" + System.currentTimeMillis());
        LoggerUtils.module(LogbackController1.class, "test2", "tt3", "ni-----------------" + System.currentTimeMillis());
        return "success";
    }
}
