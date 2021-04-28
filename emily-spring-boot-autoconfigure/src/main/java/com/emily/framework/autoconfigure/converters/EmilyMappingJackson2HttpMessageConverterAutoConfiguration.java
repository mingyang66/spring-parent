package com.emily.framework.autoconfigure.converters;

import com.emily.framework.autoconfigure.logger.common.LoggerUtils;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.http.HttpMessageConvertersAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;

import javax.annotation.PostConstruct;
import java.util.Arrays;

/**
 * @author Emily
 * @program: spring-parent
 * @description: springboot框架将字典数据类型转换为json，Content-Type默认由 content-type: application/json 更改为：content-type: application/json;charset=UTF-8
 * @create: 2020/10/28
 */
@Configuration
@AutoConfigureAfter(HttpMessageConvertersAutoConfiguration.class)
@EnableConfigurationProperties(Jackson2MessagesProperties.class)
@ConditionalOnProperty(prefix = "spring.emily.jackson2.converter", name = "enable", havingValue = "true", matchIfMissing = true)
public class EmilyMappingJackson2HttpMessageConverterAutoConfiguration implements InitializingBean, DisposableBean {
    private MappingJackson2HttpMessageConverter mappingJackson2HttpMessageConverter;

    public EmilyMappingJackson2HttpMessageConverterAutoConfiguration(MappingJackson2HttpMessageConverter mappingJackson2HttpMessageConverter) {
        this.mappingJackson2HttpMessageConverter = mappingJackson2HttpMessageConverter;
    }

    @PostConstruct
    public void EmilyMappingJackson2HttpMessageConverterAutoConfiguration() {
        mappingJackson2HttpMessageConverter.setSupportedMediaTypes(Arrays.asList(MediaType.APPLICATION_JSON_UTF8));
    }

    @Override
    public void destroy() throws Exception {
        LoggerUtils.info(EmilyMappingJackson2HttpMessageConverterAutoConfiguration.class, "【销毁--自动化配置】----响应报文Content-Type编码组件【EmilyMappingJackson2HttpMessageConverterAutoConfiguration】");
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        LoggerUtils.info(EmilyMappingJackson2HttpMessageConverterAutoConfiguration.class, "【初始化--自动化配置】----响应报文Content-Type编码组件【EmilyMappingJackson2HttpMessageConverterAutoConfiguration】");
    }
}
