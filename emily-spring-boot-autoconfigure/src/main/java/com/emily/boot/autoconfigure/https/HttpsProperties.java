package com.emily.boot.autoconfigure.https;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @description: Https属性配置文件
 * @create: 2020/06/28
 */
@ConfigurationProperties(prefix = "spring.emily.https")
public class HttpsProperties {
    private boolean enable;

    public boolean isEnable() {
        return enable;
    }

    public void setEnable(boolean enable) {
        this.enable = enable;
    }
}
