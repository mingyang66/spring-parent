package com.emily.framework.datasource.mapper;


import com.emily.framework.datasource.annotation.TargetDataSource;
import com.emily.framework.datasource.po.QuartzJob;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author Emily
 * @Description: Description
 * @Version: 1.0
 */
@Mapper
public interface MysqlMapper {
    /**
     * 查询接口
     */
    @TargetDataSource("mysql")
    String findLocks(String lockName);

    /**
     * 新增接口
     * @param schedName
     * @param lockName
     */
    @TargetDataSource("mysql")
    void insertLocks(String schedName, String lockName);

    @TargetDataSource("mysql")
    void delLocks(String lockName);
}
