package com.emily.infrastructure.redis.thread;

import com.emily.infrastructure.common.exception.PrintExceptionInfo;
import com.emily.infrastructure.common.utils.json.JSONUtils;
import com.emily.infrastructure.core.ioc.IOCContext;
import com.emily.infrastructure.redis.RedisDbProperties;
import com.emily.infrastructure.redis.entity.RedisIndicator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.RedisConnectionFactory;

import java.util.Properties;

/**
 * @program: spring-parent
 * @description: Redis监控指标线程池
 * @author: Emily
 * @create: 2021/09/12
 */
public class RedisDbRunnable implements Runnable {

    private static Logger logger = LoggerFactory.getLogger(RedisDbRunnable.class);

    private RedisConnectionFactory redisConnectionFactory;

    public RedisDbRunnable(RedisConnectionFactory redisConnectionFactory) {
        this.redisConnectionFactory = redisConnectionFactory;
    }

    @Override
    public void run() {
        RedisDbProperties redisDbProperties = IOCContext.getBean(RedisDbProperties.class);
        if (!redisDbProperties.isMonitorEnabled()) {
            return;
        }
        RedisConnection redisConnection = redisConnectionFactory.getConnection();
        while (true) {
            try {
                redisDbProperties.getConfig().forEach((key, value) -> {
                    Properties properties = redisConnection.info();
                    logger.info(JSONUtils.toJSONPrettyString(properties));
                    logger.info(RedisIndicator.toString(properties));
                });
            } catch (Exception exception) {
                logger.error(PrintExceptionInfo.printErrorInfo(exception));
            } finally {
                try {
                    Thread.sleep(redisDbProperties.getMonitorFireRate().toMillis());
                } catch (InterruptedException e) {
                    logger.error("Redis Db休眠中断...");
                }
            }
        }
    }
}
