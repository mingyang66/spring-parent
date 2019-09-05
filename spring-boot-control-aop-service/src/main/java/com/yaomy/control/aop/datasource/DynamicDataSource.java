package com.yaomy.control.aop.datasource;

import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

import javax.sql.DataSource;
import java.util.Map;

/**
 * @Description: 线程持有数据源上线文
 * @Version: 1.0
 */
public class DynamicDataSource extends AbstractRoutingDataSource {

    private static final ThreadLocal<String> CONTEXT_HOLDER = new ThreadLocal<>();

    public static DynamicDataSource build(DataSource defaultTargetDataSource, Map<Object, Object> targetDataSources){
        return new DynamicDataSource(defaultTargetDataSource, targetDataSources);
    }

    private DynamicDataSource(DataSource defaultTargetDataSource, Map<Object, Object> targetDataSources){
        super.setDefaultTargetDataSource(defaultTargetDataSource);
        super.setTargetDataSources(targetDataSources);
        super.afterPropertiesSet();
    }
    @Override
    protected Object determineCurrentLookupKey() {
        return getDataSource();
    }

    /**
     * 设置当前线程持有的数据源
     */
    public static void setDataSource(String dataSource){
        CONTEXT_HOLDER.set(dataSource);
    }
    /**
     * 获取当前线程持有的数据源
     */
    public static String getDataSource(){
        return CONTEXT_HOLDER.get();
    }

    /**
     * 删除当前线程持有的数据源
     */
    public static void remove(){
        CONTEXT_HOLDER.remove();
    }
}
