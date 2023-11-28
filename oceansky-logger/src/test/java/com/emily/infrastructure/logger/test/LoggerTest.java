package com.emily.infrastructure.logger.test;

import com.emily.infrastructure.logger.LoggerContextInitializer;
import com.emily.infrastructure.logger.LoggerFactory;
import com.emily.infrastructure.logger.common.PathUtils;
import com.emily.infrastructure.logger.configuration.property.LoggerProperties;
import org.junit.Assert;
import org.junit.Test;
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
        Assert.assertEquals(PathUtils.normalizePath(null), "");
        Assert.assertEquals(PathUtils.normalizePath(""), "");
        Assert.assertEquals(PathUtils.normalizePath("a/"), "/a");
        Assert.assertEquals(PathUtils.normalizePath("/a/"), "/a");
        Assert.assertEquals(PathUtils.normalizePath("/a/b"), "/a/b");
        Assert.assertEquals(PathUtils.normalizePath("/a/b/"), "/a/b");
    }
}
