package com.emily.boot.test.controller;

import com.emily.infrastructure.logger.LoggerFactory;
import org.slf4j.Logger;
import org.springframework.web.bind.annotation.*;

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
    private static final Logger logger = LoggerFactory.getGroupLogger(LogbackController.class,"66/12/34", "test");
    private static final Logger groupLogger = LoggerFactory.getGroupLogger(LogbackController.class,"emily/test/demo");
    //private static final Logger logger1 = org.slf4j.LoggerFactory.getLogger("moduleOne");
    //private static final Logger logger2 = org.slf4j.LoggerFactory.getLogger("moduleOne66666");
    @GetMapping("debug")
    public String debug() {
        baseLogger.error("--------error");
        baseLogger.info("--------info");
        baseLogger.debug("--------debug");
        baseLogger.warn("--------warn");
        baseLogger.trace("--------trace");
       /* logger1.info("---------test--");
        logger2.info("---------test2--");
        logger1.error("3444444");
        logger2.error("66666666");*/
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


        LoggerFactory.getModuleLogger(LogbackController.class,"test1", "tt0").error("ni-----------------" + System.currentTimeMillis());

        return "success";
    }

}
