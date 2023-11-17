package com.emily.infrastructure.test.config.route;

import com.emily.infrastructure.core.servlet.FilterAutoConfiguration;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.Bean;

/**
 * 路由重定向自动化配置
 *
 * @author Emily
 * @since Created in 2023/2/4 2:31 下午
 */
@AutoConfiguration(before = FilterAutoConfiguration.class)
public class RouteAutoConfiguration {

    @Bean
    public DefaultRoutingRedirectCustomizer defaultRoutingRedirectCustomizer() {
        return new DefaultRoutingRedirectCustomizer();
    }
}
