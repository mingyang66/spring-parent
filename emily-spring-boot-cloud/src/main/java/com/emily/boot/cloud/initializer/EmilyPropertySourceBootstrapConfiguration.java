package com.emily.boot.cloud.initializer;

import com.google.common.collect.Lists;
import org.springframework.cloud.bootstrap.config.PropertySourceBootstrapConfiguration;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.core.Ordered;
import org.springframework.core.env.ConfigurableEnvironment;

import java.util.Arrays;
import java.util.List;

/**
 * @description: 初始化器，在{@link PropertySourceBootstrapConfiguration} 之前替换掉active配置文件中的占位符
 * @create: 2020/11/13
 */
public class EmilyPropertySourceBootstrapConfiguration implements ApplicationContextInitializer<ConfigurableApplicationContext>, Ordered {
    private int order = Ordered.HIGHEST_PRECEDENCE + 9;

    @Override
    public void initialize(ConfigurableApplicationContext applicationContext) {
        if(applicationContext instanceof AnnotationConfigApplicationContext){
            ConfigurableEnvironment env = applicationContext.getEnvironment();
            List<String> profiles = Arrays.asList(env.getActiveProfiles());
            List<String> newProfiles = Lists.newArrayList();
            profiles.forEach(profile -> {
                newProfiles.add(env.resolvePlaceholders(profile));
            });
            env.setActiveProfiles(newProfiles.toArray(new String[]{}));
        }
    }

    @Override
    public int getOrder() {
        return order;
    }
}
