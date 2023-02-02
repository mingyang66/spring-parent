package com.emily.infrastructure.autoconfigure.handler;

import com.emily.infrastructure.autoconfigure.handler.mapping.LookupPathCustomizer;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Role;

/**
 * @Description :  自定义方法初始化配置类
 * @Author :  姚明洋
 * @CreateDate :  Created in 2023/2/2 2:22 下午
 */
@Role(BeanDefinition.ROLE_INFRASTRUCTURE)
@AutoConfiguration(before = RequestMappingAutoConfiguration.class)
public class LookupPathAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    @Role(BeanDefinition.ROLE_INFRASTRUCTURE)
    public LookupPathCustomizer lookupPathCustomizer() {
        return new LookupPathCustomizer() {
            @Override
            public String resolveSpecifiedLookupPath(String lookupPath) {
                return lookupPath;
            }
        };
    }
}
