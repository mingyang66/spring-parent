package com.emily.infrastructure.test.controller.route;

import com.emily.infrastructure.core.servlet.FilterAutoConfiguration;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.Bean;

/**
 * @Description :  路由重定向自动化配置
 * @Author :  Emily
 * @CreateDate :  Created in 2023/2/4 2:31 下午
 */
@AutoConfiguration(before = FilterAutoConfiguration.class)
public class RouteAutoConfiguration {

    @Bean
    public DefaultRoutingRedirectCustomizer defaultRoutingRedirectCustomizer() {
        return new DefaultRoutingRedirectCustomizer();
    }
}
