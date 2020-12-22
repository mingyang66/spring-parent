package com.emily.framework.autoconfigure.accesslog;

import com.emily.framework.common.utils.log.accesslog.po.AccessLog;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @description: 日志配置属性
 * @create: 2020/08/08
 */
@ConfigurationProperties(prefix = "spring.emily.accesslog")
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
