package com.sgrain.boot.autoconfigure.https;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @program: spring-parent
 * @description: Https属性配置文件
 * @author: 姚明洋
 * @create: 2020/06/28
 */
@ConfigurationProperties(prefix = "spring.sgrain.https")
public class HttpsProperties {
    private boolean enable;

    public boolean isEnable() {
        return enable;
    }

    public void setEnable(boolean enable) {
        this.enable = enable;
    }
}
