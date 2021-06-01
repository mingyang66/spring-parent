package com.emily.infrastructure.test.controller;

import com.emily.infrastructure.logback.common.LoggerUtils;
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
        LoggerUtils.info(LogbackController.class, "info--------");
        LoggerUtils.debug(LogbackController.class, "debug--------");
        LoggerUtils.error(LogbackController.class, "error--------");
        LoggerUtils.warn(LogbackController.class, "warn--------");
        LoggerUtils.trace(LogbackController.class, "trace--------");
        return "success";
    }
}
