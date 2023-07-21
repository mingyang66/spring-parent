package com.emily.infrastructure.test.controller;

import com.emily.infrastructure.logger.LoggerFactory;
import org.slf4j.Logger;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;
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
@RequestMapping("api/logback")
public class LogbackController {
    private static final Logger baseLogger = LoggerFactory.getLogger(LogbackController.class);
    private static final Logger logger = LoggerFactory.getGroupLogger(LogbackController.class, "group/test");
    private static final Logger groupLogger = LoggerFactory.getGroupLogger(LogbackController.class, "group/test1");
    private static final Marker marker = MarkerFactory.getMarker("instance_marker");

    @GetMapping("debug")
    public String debug() {
        baseLogger.error("--------error");
        baseLogger.info("--------info");
        baseLogger.debug("--------debug");
        baseLogger.warn("--------warn");
        baseLogger.trace("--------trace");

        try {
            String s = null;
            s.length();
        } catch (Exception e) {
            baseLogger.error("-----error test----", e);
        }

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


    @GetMapping("marker")
    public void get() {
        baseLogger.error("--------error");
        baseLogger.info("--------info");
        baseLogger.debug("--------debug");
        baseLogger.warn("--------warn");
        baseLogger.trace("--------trace");

        baseLogger.error(marker, "--------marker error");
        baseLogger.info(marker, "--------marker info");
        baseLogger.debug(marker, "--------marker debug");
        baseLogger.warn(marker, "--------marker warn");
        baseLogger.trace(marker, "--------marker trace");
    }
}
