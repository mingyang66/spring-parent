package com.emily.infrastructure.datasource.lookup;

import com.emily.infrastructure.datasource.context.DataSourceContextHolder;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

/**
 * 抽象的数据源实现（javax.sql.DataSource），该实现基于查找键将getConnection()路由到各种目标数据源，目标数据源通常但是不限于通过一些线程绑定
 * 的事务上下文来确定
 *
 * @author Emily
 * @since 1.0
 */
public class DynamicMultiRoutingDataSource extends AbstractRoutingDataSource {
    /**
     * 解析给定的查找键对象，就像在{@link #setTargetDataSources targetDataSources} map中，
     * 输入要用于{@link #determineCurrentLookupKey() 当前查找键}
     * 默认的实现只是返回给定的查找键
     *
     * @param lookupKey 查找键
     * @return 转换后的查找键
     */
    @Override
    protected Object resolveSpecifiedLookupKey(Object lookupKey) {
        return lookupKey;
    }

    /**
     * 确定当前线程的查找键，这通常用于检查线程绑定事物的上下文，允许是任意的键（Look Up Key），
     * 返回的查找键（Look Up Key）需要与存储的查找键（Look Up key）类型匹配
     */
    @Override
    protected Object determineCurrentLookupKey() {
        return DataSourceContextHolder.current();
    }

}
