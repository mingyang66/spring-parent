package com.emily.infrastructure.logger.initializer;

import com.emily.infrastructure.logback.LogbackContextInitializer;
import com.emily.infrastructure.logger.LoggerProperties;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.Ordered;

/**
 * --------------------------------------------------
 * ApplicationContextInitializer的初始化顺序整体在ApplicationListener监听器之后
 * 具体调用在org.springframework.boot.SpringApplication#run(java.lang.String...)方法内部
 * <pre>{@code
 * SpringApplicationRunListeners listeners = this.getRunListeners(args);
 * listeners.starting(bootstrapContext, this.mainApplicationClass);
 *
 * this.prepareContext(bootstrapContext, context, environment, listeners, applicationArguments, printedBanner);
 * }</pre>
 * --------------------------------------------------
 * <p>
 * Logback日志组件初始化类
 *
 * @author Emily
 * @since 4.0.7
 */
public class LogbackApplicationContextInitializer implements ApplicationContextInitializer<ConfigurableApplicationContext>, Ordered {

    @Override
    public void initialize(ConfigurableApplicationContext context) {
        // 将属性配置绑定到配置类上
        LoggerProperties properties = Binder.get(context.getEnvironment()).bindOrCreate(LoggerProperties.PREFIX, LoggerProperties.class);
        // SDK组件开关打开时才会初始化日志组件、线程池
        if (properties.isEnabled()) {
            // 初始化日志SDK上下文
            LogbackContextInitializer.init(properties);
        }
    }

    /**
     * 在spring-cloud场景下需满足：
     * 1. 初始化优先级低于org.springframework.cloud.bootstrap.config.PropertySourceBootstrapConfiguration类
     *
     * @return 优先级
     */
    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE + 11;
    }
}
