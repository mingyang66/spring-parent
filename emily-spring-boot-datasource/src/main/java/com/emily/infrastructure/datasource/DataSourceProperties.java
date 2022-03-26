package com.emily.infrastructure.datasource;

import com.alibaba.druid.pool.DruidDataSource;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.boot.context.properties.ConfigurationProperties;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

/**
 * @description: 数据源配置文件
 * @author: Emily
 * @create: 2020/05/14
 */
@ConfigurationProperties(prefix = DataSourceProperties.PREFIX)
public class DataSourceProperties {
    /**
     * 前缀
     */
    public static final String PREFIX = "spring.emily.datasource";
    /**
     * 默认数据源配置，默认：default
     */
    public static final String DEFAULT_DATASOURCE = "default";
    /**
     * 数据库连接池集合，包含druid+hikari
     */
    public static final Map<String, DataSource> ALL_DATASOURCE = new HashMap<>();
    /**
     * 是否开启数据源组件, 默认：true
     */
    private boolean enabled = true;
    /**
     * 默认数据源配置，默认：default
     */
    private String defaultDataSource = DEFAULT_DATASOURCE;
    /**
     * 是否拦截超类或者接口中的方法，默认：true
     */
    private boolean checkInherited = true;
    /**
     * 是否对默认数据源执行宽松回退，即：当目标数据源找不到时回退到默认数据源，默认：true
     */
    private boolean lenientFallback = true;
    /**
     * Druid数据库连接池多数据源配置
     */
    private Map<String, DruidDataSource> druid = new HashMap<>();
    /**
     * Hikari数据库连接池多数据源配置
     */
    private Map<String, HikariDataSource> hikari = new HashMap<>();

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public String getDefaultDataSource() {
        return defaultDataSource;
    }

    public void setDefaultDataSource(String defaultDataSource) {
        this.defaultDataSource = defaultDataSource;
    }

    public boolean isCheckInherited() {
        return checkInherited;
    }

    public void setCheckInherited(boolean checkInherited) {
        this.checkInherited = checkInherited;
    }

    public boolean isLenientFallback() {
        return lenientFallback;
    }

    public void setLenientFallback(boolean lenientFallback) {
        this.lenientFallback = lenientFallback;
    }

    public Map<String, DruidDataSource> getDruid() {
        return druid;
    }

    public void setDruid(Map<String, DruidDataSource> druid) {
        this.druid = druid;
    }

    public Map<String, HikariDataSource> getHikari() {
        return hikari;
    }

    public void setHikari(Map<String, HikariDataSource> hikari) {
        this.hikari = hikari;
    }

    /**
     * 获取合并后的数据源配置
     *
     * @return
     */
    public Map<String, DataSource> getMergeDataSource() {
        if (ALL_DATASOURCE.isEmpty()) {
            ALL_DATASOURCE.putAll(this.getDruid());
            ALL_DATASOURCE.putAll(this.getHikari());
        }
        return ALL_DATASOURCE;
    }
}
