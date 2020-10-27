package com.sgrain.boot.autoconfigure.initializers;

import com.sgrain.boot.common.utils.LoggerUtils;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.Ordered;

/**
 * @program: spring-parent
 * @description: 小米粒框架初始化器
 * @create: 2020/09/22
 */
public class SmallGrainApplicationContenxtInitializer implements ApplicationContextInitializer<ConfigurableApplicationContext>, Ordered {
    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE+1;
    }

    @Override
    public void initialize(ConfigurableApplicationContext applicationContext) {
        LoggerUtils.info(SmallGrainApplicationContenxtInitializer.class, "Small Grain【小米粒】初始化器开始初始化IOC容器了,容器名为："+applicationContext.getClass().getName());
    }
}
