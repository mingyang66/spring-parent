package com.emily.infrastructure.web.response;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.ArrayList;
import java.util.List;

/**
 * 返回值配置文件类
 *
 * @author Emily
 * @since 2020/03/25
 */
@ConfigurationProperties(prefix = ResponseProperties.PREFIX)
public class ResponseProperties {
    /**
     * 属性配置
     */
    public static final String PREFIX = "spring.emily.response";
    /**
     * 组件开关，默认：true
     */
    private boolean enabled = true;
    /**
     * 忽略包装指定URL
     */
    private List<String> exclude = new ArrayList<>();

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public List<String> getExclude() {
        return exclude;
    }

    public void setExclude(List<String> exclude) {
        this.exclude = exclude;
    }
}
