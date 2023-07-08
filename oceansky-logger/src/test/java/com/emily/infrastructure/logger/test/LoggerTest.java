package com.emily.infrastructure.logger.test;

import com.emily.infrastructure.logger.LoggerFactory;
import com.emily.infrastructure.logger.configuration.property.LoggerProperties;
import com.emily.infrastructure.logger.manager.LoggerContextManager;
import org.junit.Test;
import org.slf4j.Logger;

/**
 * @Description :
 * @Author :  Emily
 * @CreateDate :  Created in 2023/7/2 3:45 PM
 */
public class LoggerTest {
    public static final Logger logger = LoggerFactory.getLogger(LoggerTest.class);

    @Test
    public void test1() {
        LoggerContextManager.init(new LoggerProperties());
        logger.info("info test ----------------");
        logger.error("info test ----------------");
        logger.warn("warn test ----------------");
        logger.debug("debug test ----------------");
        logger.trace("trace test ----------------");
    }
}
