package com.emily.infrastructure.logback.initializer;

import com.emily.infrastructure.logback.LogbackProperties;
import com.emily.infrastructure.logback.util.PropertyUtils;
import com.emily.infrastructure.logger.LoggerContextManager;
import com.emily.infrastructure.logger.LoggerFactory;
import com.emily.infrastructure.logger.configuration.context.LogbackContext;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.Ordered;
import org.springframework.core.env.Environment;

/**
 * @Description: Logback日志组件初始化类
 * @Author: Emily
 * @create: 2022/2/8
 * @since 4.0.7
 */
public class LogbackApplicationContextInitializer implements ApplicationContextInitializer<ConfigurableApplicationContext>, Ordered {
    /**
     * 初始化次数
     */
    private static int INITIAL_TIMES = 0;
    /**
     * cloud微服务最大初始化次数
     */
    private static int MAX_INITIAL_TIMES = 2;

    /**
     * 初始化优先级低于org.springframework.cloud.bootstrap.config.PropertySourceBootstrapConfiguration类
     *
     * @return 优先级
     */
    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE + 11;
    }

    @Override
    public void initialize(ConfigurableApplicationContext context) {
        // 将属性配置绑定到配置类上
        LogbackProperties properties = Binder.get(context.getEnvironment()).bindOrCreate(LogbackProperties.PREFIX, LogbackProperties.class);
        // 初始化日志SDK上下文
        LoggerContextManager.init(properties);

    }

    /**
     * 1.日志组件开启，微服务未开启 2.日志组件开启，微服务开启并且第二次初始化
     */
    private void initContext(Environment environment, LogbackProperties properties) {
        // 1.日志组件开启，微服务未开启 2.日志组件开启，微服务开启并且第二次初始化
        if (!properties.isEnabled()) {
            return;
        }
        System.out.println(PropertyUtils.bootstrapEnabled(environment));
        System.out.println(PropertyUtils.useLegacyProcessing(environment));
        //非微服务初始化
        if (!(PropertyUtils.bootstrapEnabled(environment) || PropertyUtils.useLegacyProcessing(environment))) {
            initLogbackFactory(properties);
            return;
        }
        //微服务初始化
        if (++INITIAL_TIMES == MAX_INITIAL_TIMES) {
            initLogbackFactory(properties);
        }
    }

    /**
     * 初始化日志组件；
     *
     * @param properties
     */
    private void initLogbackFactory(LogbackProperties properties) {
        LoggerFactory.CONTEXT = new LogbackContext(properties);
    }
}
