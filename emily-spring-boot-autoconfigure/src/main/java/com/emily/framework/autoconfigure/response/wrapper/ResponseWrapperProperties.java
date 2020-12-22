package com.emily.framework.autoconfigure.response.wrapper;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.HashSet;
import java.util.Set;

/**
 * @program: spring-parent
 * @description: 返回值配置文件类
 * @create: 2020/03/25
 */
@ConfigurationProperties(prefix = "spring.emily.response.wrapper")
public class ResponseWrapperProperties {
    /**
     * 组件开关
     */
    private boolean enable;
    /**
     * 忽略包装指定URL
     */
    private Set<String> exclude = new HashSet<>();

    public boolean isEnable() {
        return enable;
    }

    public void setEnable(boolean enable) {
        this.enable = enable;
    }

    public Set<String> getExclude() {
        return exclude;
    }

    public void setExclude(Set<String> exclude) {
        this.exclude = exclude;
    }
}
