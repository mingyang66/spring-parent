package com.emily.infrastructure.autoconfigure.exception;

import com.emily.infrastructure.autoconfigure.exception.handler.DefaultGlobalExceptionHandler;
import com.emily.infrastructure.autoconfigure.exception.handler.GlobalExceptionCustomizer;
import com.emily.infrastructure.language.convert.LanguageMap;
import com.emily.infrastructure.logger.LoggerFactory;
import org.slf4j.Logger;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Role;

import javax.annotation.PostConstruct;

/**
 * @author Emily
 * @program: spring-parent
 * @description: 异常捕获自动化配置类
 * @create: 2020/10/28
 */
@AutoConfiguration
@EnableConfigurationProperties(GlobalExceptionProperties.class)
@ConditionalOnProperty(prefix = GlobalExceptionProperties.PREFIX, name = "enabled", havingValue = "true", matchIfMissing = true)
public class GlobalExceptionAutoConfiguration implements InitializingBean, DisposableBean {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionAutoConfiguration.class);

    /**
     * 初始化异常多语言
     */
    @PostConstruct
    public void init() {
        LanguageMap.bindEn("网络异常，请稍后再试", "Network exception, please try again later");
        LanguageMap.bindEn("非法方法请求", "Illegal method request");
        LanguageMap.bindEn("非法参数", "Illegal parameter");
        LanguageMap.bindEn("非法数据", "invalid data");
        LanguageMap.bindEn("非法访问", "Illegal access");
        LanguageMap.bindEn("非法代理", "Illegal agency");
    }

    /**
     * 异常抛出拦截bean初始化
     *
     * @return
     */
    @Bean
    @Role(BeanDefinition.ROLE_INFRASTRUCTURE)
    @ConditionalOnMissingBean(GlobalExceptionCustomizer.class)
    public DefaultGlobalExceptionHandler defaultGlobalExceptionHandler() {
        return new DefaultGlobalExceptionHandler();
    }


    @Override
    public void destroy() throws Exception {
        logger.info("<== 【销毁--自动化配置】----异常捕获组件【GlobalExceptionAutoConfiguration】");
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        logger.info("==> 【初始化--自动化配置】----异常捕获组件【GlobalExceptionAutoConfiguration】");
    }
}
