package com.emily.infrastructure.logger.test;

import com.emily.infrastructure.logback.LogbackContextInitializer;
import com.emily.infrastructure.logback.factory.LoggerFactory;
import com.emily.infrastructure.logback.common.PathUtils;
import com.emily.infrastructure.logback.LogbackProperties;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;

/**
 * :
 *
 * @author Emily
 * @since :  Created in 2023/7/2 3:45 PM
 */
public class LoggerTest {

    private static final Logger logger = LoggerFactory.getLogger(LoggerTest.class);

    @Test
    public void test1() {
        LogbackContextInitializer.init(new LogbackProperties());
        LogbackContextInitializer.init(new LogbackProperties());
        logger.info("info test ----------------");
        logger.error("info test ----------------");
        logger.warn("warn test ----------------");
        logger.debug("debug test ----------------");
        logger.trace("trace test ----------------");
    }

    @Test
    public void pathTest() {
        Assertions.assertEquals(PathUtils.normalizePath(null), "");
        Assertions.assertEquals(PathUtils.normalizePath(""), "");
        Assertions.assertEquals(PathUtils.normalizePath("a/"), "/a");
        Assertions.assertEquals(PathUtils.normalizePath("/a/"), "/a");
        Assertions.assertEquals(PathUtils.normalizePath("/a/b"), "/a/b");
        Assertions.assertEquals(PathUtils.normalizePath("/a/b/"), "/a/b");
    }
}
