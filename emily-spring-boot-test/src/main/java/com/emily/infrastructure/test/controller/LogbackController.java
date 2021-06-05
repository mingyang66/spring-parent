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
public class LogbackController {

    @GetMapping("debug")
    public String debug(){
        LoggerUtils.getLogger("/11/22/ss/", "test").info("211112122");
        LoggerUtils.getLogger().debug("shuai1 +++++++++++++++++++++++++++++++++++++debug\"");
        LoggerUtils.getLogger("emily", "smile").info("+++++++++++==ttttttttttttt");
        //LoggerUtils.builder.getLogger(LogbackController.class).debug("shuai1 +++++++++++++++++++++++++++++++++++++debug");
        LoggerUtils.warn(LogbackController.class,"shuai2 +++++++++++++++++++++++++++++++++++++warn");
        LoggerUtils.info(LogbackController.class,"shuai3 +++++++++++++++++++++++++++++++++++++info");
        LoggerUtils.error(LogbackController.class,"shuai4 +++++++++++++++++++++++++++++++++++++error");
        LoggerUtils.trace(LogbackController.class,"shuai5 +++++++++++++++++++++++++++++++++++++trace");
        LoggerUtils.module(LogbackController.class, "test1", "tt0", "ni-----------------" + System.currentTimeMillis());
        LoggerUtils.module(LogbackController.class, "test1", "tt1", "ni-----------------" + System.currentTimeMillis());
        LoggerUtils.module(LogbackController.class, "test2", "tt2", "ni-----------------" + System.currentTimeMillis());
        LoggerUtils.module(LogbackController.class, "test2", "tt3", "ni-----------------" + System.currentTimeMillis());
        return "success";
    }
}
