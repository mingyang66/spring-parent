package com.emily.framework.datasource.context;

import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

import javax.sql.DataSource;
import java.util.Map;

/**
 * @Description: 抽象的数据源实现（javax.sql.DataSource），该实现基于查找键将getConnection()路由到各种目标数据源，目标数据源通常但是不限于通过一些线程绑定
 * 的事务上下文来确定
 * @Author Emily
 * @Version: 1.0
 */
public class DynamicDataSource extends AbstractRoutingDataSource {
    /**
     * 私有的构造函数
     * @param defaultTargetDataSource 默认数据源
     * @param targetDataSources 所有的数据源
     */
    private DynamicDataSource(DataSource defaultTargetDataSource, Map<Object, Object> targetDataSources){
        /**
         * 如果存在默认数据源，指定默认的目标数据源；映射的值可以是javax.sql.DataSource或者是数据源（data source）字符串；
         * 如果setTargetDataSources指定的数据源不存在，将会使用默认的数据源
         */
        super.setDefaultTargetDataSource(defaultTargetDataSource);
        /**
         * 指定目标数据源的Map集合映射，使用查找键（Look Up Key）作为Key,这个Map集合的映射Value可以是javax.sql.DataSource或者是数据源（data source）字符串；
         * 集合的Key可以为任何数据类型，当前类会通过泛型的方式来实现查找，
         */
        super.setTargetDataSources(targetDataSources);
        /**
         * 指定对默认的数据源是否应用宽松的回退，如果找不到当前查找键（Look Up Key）的特定数据源，就返回默认的数据源，默认为true;
         */
        super.setLenientFallback(true);
        /**
         * 设置DataSourceLookup为解析数据源的字符串，默认是使用JndiDataSourceLookup；允许直接指定应用程序服务器数据源的JNDI名称；
         */
        super.setDataSourceLookup(null);
        /**
         * 将设置的默认数据源、目标数据源解析为真实的数据源对象赋值给resolvedDefaultDataSource变量和resolvedDataSources变量
         */
        super.afterPropertiesSet();
        /**
         * 将数据源查找键（look up key）KEY存储进入静态变量中，供其它地方校验使用
         */
        DataSourceContextHolder.ALL_DATA_SOURCE_KEY.addAll(targetDataSources.keySet());
    }

    /**
     * 构件DynamicDataSource对象静态方法
     */
    public static DynamicDataSource build(DataSource defaultTargetDataSource, Map<Object, Object> targetDataSources){
        return new DynamicDataSource(defaultTargetDataSource, targetDataSources);
    }

    /**
     * 确定当前线程的查找键，这通常用于检查线程绑定事物的上下文，允许是任意的键（Look Up Key），
     * 返回的查找键（Look Up Key）需要与存储的查找键（Look Up key）类型匹配
     */
    @Override
    protected Object determineCurrentLookupKey() {
        return DataSourceContextHolder.getDataSource();
    }

}
