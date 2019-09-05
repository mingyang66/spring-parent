package com.yaomy.control.aop.datasource;

import com.yaomy.control.aop.exception.UnknownDataSourceException;
import org.apache.commons.lang3.StringUtils;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

import javax.sql.DataSource;
import java.util.*;

/**
 * @Description: 线程持有数据源上线文
 * @Author yaomy
 * @Version: 1.0
 */
public class DynamicDataSource extends AbstractRoutingDataSource {
    /**
     * 当前线程对应的数据源
     */
    private static final ThreadLocal<String> CONTEXT_HOLDER = new ThreadLocal<>();
    /**
     * 存储当前系统加载的数据源的查找键（look up key）KEY
     */
    private static final Set<Object> ALL_DATA_SOURCE_KEY = new HashSet<>();


    private DynamicDataSource(DataSource defaultTargetDataSource, Map<Object, Object> targetDataSources){
        super.setDefaultTargetDataSource(defaultTargetDataSource);
        super.setTargetDataSources(targetDataSources);
        super.afterPropertiesSet();
        /**
         * 将数据源查找键（look up key）KEY存储进入静态变量中，供其它地方校验使用
         */
        ALL_DATA_SOURCE_KEY.addAll(targetDataSources.keySet());
    }

    /**
     * 设置当前线程持有的数据源
     */
    public static void setDataSource(String dataSource){
        if(isExist(dataSource)){
            CONTEXT_HOLDER.set(dataSource);
        } else {
            throw new UnknownDataSourceException(StringUtils.join("数据源查找键（Look up key）【", dataSource,"】不存在"));
        }
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

    /**
     * 判断数据源在系统中是否存在
     */
    public static boolean isExist(String dataSource){
        if(StringUtils.isEmpty(dataSource)){
            return false;
        }
        if(ALL_DATA_SOURCE_KEY.contains(dataSource)){
            return true;
        }
        return false;
    }
    /**
     * 构件DynamicDataSource对象
     */
    public static DynamicDataSource build(DataSource defaultTargetDataSource, Map<Object, Object> targetDataSources){
        return new DynamicDataSource(defaultTargetDataSource, targetDataSources);
    }

    /**
     * 确定当前线程的查找键，这通常用于检查线程绑定事物的上下文，允许是任意的键（Look Up Key）
     */
    @Override
    protected Object determineCurrentLookupKey() {
        return getDataSource();
    }

}
