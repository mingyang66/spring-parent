package com.emily.infrastructure.cloud.listener;

import com.emily.infrastructure.core.context.ioc.IocUtils;
import org.springframework.cloud.context.environment.EnvironmentChangeEvent;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;

import java.util.Objects;

/**
 * Environment环境配置更改监听器
 *
 * @author Emily
 * @since Created in 2023/6/25 5:54 PM
 */
public class EnvironmentChangeApplicationListener implements ApplicationListener<EnvironmentChangeEvent> {
    @Override
    public void onApplicationEvent(EnvironmentChangeEvent event) {
        Object source = event.getSource();
        if (Objects.isNull(source) || !(source instanceof ApplicationContext)) {
            return;
        }
        IocUtils.setApplicationContext((ApplicationContext) source);
    }
}
