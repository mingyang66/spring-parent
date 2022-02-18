package com.emily.cloud.test.controller;

import com.emily.infrastructure.core.context.ioc.IOCContext;
import com.emily.infrastructure.logger.LoggerFactory;
import org.slf4j.Logger;
import org.springframework.core.env.Environment;
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
    private static final Logger baseLogger = LoggerFactory.getLogger(LogbackController.class);
    private static final Logger logger = LoggerFactory.getGroupLogger(LogbackController.class, "group/test", "com/emily/cloud/test");
    private static final Logger groupLogger = LoggerFactory.getGroupLogger(LogbackController.class, "group/test1/demo");

    @GetMapping("debug")
    public String debug() {
        baseLogger.error("--------error");
        baseLogger.info("--------info");
        baseLogger.debug("--------debug");
        baseLogger.warn("--------warn");
        baseLogger.trace("--------trace");

        logger.error("211112122error");
        logger.debug("211112122debug");
        logger.info("211112122info");
        logger.warn("211112122warn");
        logger.trace("211112122+trace");
        groupLogger.error("+++++++++++==ttttttttttttt");
        groupLogger.debug("+++++++++++==ttttttttttttt");
        groupLogger.info("+++++++++++==ttttttttttttt");
        groupLogger.warn("+++++++++++==ttttttttttttt");
        groupLogger.trace("+++++++++++==ttttttttttttt");


        LoggerFactory.getModuleLogger(LogbackController.class, "test1", "tt0").info("ni-----------------" + System.currentTimeMillis());

        return "success";
    }

    @GetMapping("get")
    public String get() {
        Environment environment = IOCContext.getApplicationContext().getEnvironment();
        return environment.getProperty("spring.emily.logback.enabled");
    }
}
