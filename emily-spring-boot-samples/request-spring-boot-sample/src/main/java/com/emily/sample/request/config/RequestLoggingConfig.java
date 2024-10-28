package com.emily.sample.request.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.filter.CommonsRequestLoggingFilter;

/**
 * @author :  Emily
 * @since :  2024/10/25 上午10:05
 */
@Configuration
public class RequestLoggingConfig {
    @Bean
    public CommonsRequestLoggingFilter requestLoggingFilter() {
        CommonsRequestLoggingFilter loggingFilter = new CommonsRequestLoggingFilter();
        loggingFilter.setIncludeQueryString(true);
        loggingFilter.setIncludePayload(true);
        loggingFilter.setIncludeHeaders(true);
        loggingFilter.setIncludeClientInfo(true);
        loggingFilter.setAfterMessagePrefix("Request DATA-");
        return loggingFilter;
    }
}
