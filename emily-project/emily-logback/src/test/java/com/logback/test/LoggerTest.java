package com.logback.test;

import com.emily.infrastructure.logback.LogbackContextInitializer;
import com.emily.infrastructure.logback.LogbackProperties;
import com.emily.infrastructure.logback.common.PathUtils;
import com.emily.infrastructure.logback.factory.LoggerFactory;
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
        LogbackContextInitializer.initialize(new LogbackProperties());
        Object context = LogbackContextInitializer.getLogbackContext();
        LogbackContextInitializer.initialize(new LogbackProperties());
        Assertions.assertSame(context, LogbackContextInitializer.getLogbackContext());
        logger.info("info test ----------------");
        logger.error("info test ----------------");
        logger.warn("warn test ----------------");
        logger.debug("debug test ----------------");
        logger.trace("trace test ----------------");
    }

    @Test
    void shouldNormalizePath() {
        Assertions.assertEquals("", PathUtils.normalizePath(null));
        Assertions.assertEquals("", PathUtils.normalizePath(""));
        Assertions.assertEquals("/a", PathUtils.normalizePath("a/"));
        Assertions.assertEquals("/a", PathUtils.normalizePath("/a/"));
        Assertions.assertEquals("/a/b", PathUtils.normalizePath("/a/b"));
        Assertions.assertEquals("/a/b", PathUtils.normalizePath("/a/b/"));
        Assertions.assertEquals("/a/b", PathUtils.normalizePath("\\a\\b\\"));
        Assertions.assertEquals("/a/b", PathUtils.normalizePath("//a///b//"));
        Assertions.assertEquals("", PathUtils.normalizePath("///"));
    }

}
