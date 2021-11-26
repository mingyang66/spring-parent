package com.emily.infrastructure.core.context;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.boot.autoconfigure.AutoConfigureOrder;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Role;
import org.springframework.core.Ordered;

/**
 * @program: spring-parent
 * @description: 全链路追踪上下文自动化配置
 * @author: Emily
 * @create: 2021/11/27
 */
@Role(BeanDefinition.ROLE_INFRASTRUCTURE)
@Configuration
@AutoConfigureOrder(Ordered.HIGHEST_PRECEDENCE)
@EnableConfigurationProperties(TraceContextProperties.class)
@ConditionalOnProperty(prefix = TraceContextProperties.PREFIX, name = "enabled", havingValue = "true", matchIfMissing = true)
public class TraceContextAutoConfiguration {
}
