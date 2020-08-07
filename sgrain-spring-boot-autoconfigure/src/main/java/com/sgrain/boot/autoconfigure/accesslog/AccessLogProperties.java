package com.sgrain.boot.autoconfigure.accesslog;

import com.sgrain.boot.common.accesslog.po.AccessLog;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @program: spring-parent
 * @description: 日志配置属性
 * @author: 姚明洋
 * @create: 2020/08/08
 */
@ConfigurationProperties(prefix = "spring.sgrain.accesslog")
public class AccessLogProperties extends AccessLog {
    /**
     * 是否开启 AccessLog日志组件
     */
    private boolean enable;

    public boolean isEnable() {
        return enable;
    }

    public void setEnable(boolean enable) {
        this.enable = enable;
    }
}
