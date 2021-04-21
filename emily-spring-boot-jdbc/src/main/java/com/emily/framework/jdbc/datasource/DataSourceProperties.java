package com.emily.framework.jdbc.datasource;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @program: com.eastmoney.emis.utils
 * @description: 数据源配置文件
 * @author: Emily
 * @create: 2020/05/14
 */
@ConfigurationProperties(prefix = "spring.emis.datasource")
public class DataSourceProperties {
    //是否开启数据源组件
    private boolean enable;

    public boolean isEnable() {
        return enable;
    }

    public void setEnable(boolean enable) {
        this.enable = enable;
    }
}
