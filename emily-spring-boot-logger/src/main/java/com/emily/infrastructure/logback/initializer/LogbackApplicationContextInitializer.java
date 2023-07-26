package com.emily.infrastructure.logback.initializer;

import com.emily.infrastructure.logback.LogbackProperties;
import com.emily.infrastructure.logger.manager.LoggerContextInitializer;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.Ordered;

/**
 * --------------------------------------------------
 * ApplicationContextInitializer的初始化顺序整体在ApplicationListener监听器之后
 * 具体调用在org.springframework.boot.SpringApplication#run(java.lang.String...)方法内部
 * SpringApplicationRunListeners listeners = this.getRunListeners(args);
 * listeners.starting(bootstrapContext, this.mainApplicationClass);
 * ...
 * this.prepareContext(bootstrapContext, context, environment, listeners, applicationArguments, printedBanner);
 * ...
 * --------------------------------------------------
 * <p>
 * Logback日志组件初始化类
 *
 * @author : Emily
 * @since 4.0.7
 */
public class LogbackApplicationContextInitializer implements ApplicationContextInitializer<ConfigurableApplicationContext>, Ordered {

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
        LoggerContextInitializer.init(properties);
    }
}
