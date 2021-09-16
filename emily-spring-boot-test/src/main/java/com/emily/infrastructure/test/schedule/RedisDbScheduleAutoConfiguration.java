package com.emily.infrastructure.test.schedule;

import com.emily.infrastructure.common.utils.json.JSONUtils;
import com.emily.infrastructure.context.ioc.IOCContext;
import com.emily.infrastructure.datasource.redis.RedisDbProperties;
import com.emily.infrastructure.datasource.redis.factory.RedisDbFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import java.util.Properties;
import java.util.Set;

/**
 * @program: spring-parent
 * @description: 基于注解的定时任务-Redis DB监控
 * @author: Emily
 * @create: 2021/09/15
 */
@EnableScheduling
@Configuration
public class RedisDbScheduleAutoConfiguration {

    private static Logger logger = LoggerFactory.getLogger(RedisDbScheduleAutoConfiguration.class);

    @Scheduled(initialDelay = 1000, fixedDelay = 30000)
    public void myTask() {
        System.out.println("------");
    }
}
