package com.emily.infrastructure.logger.test;

import com.emily.infrastructure.logger.LoggerContextInitializer;
import com.emily.infrastructure.logger.LoggerFactory;
import com.emily.infrastructure.logger.common.PathUtils;
import com.emily.infrastructure.logger.LoggerProperties;
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
        LoggerContextInitializer.init(new LoggerProperties());
        LoggerContextInitializer.init(new LoggerProperties());
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
