package com.emily.infrastructure.test.mapper.mysql;


import com.emily.infrastructure.datasource.annotation.TargetDataSource;
import org.apache.ibatis.annotations.Mapper;

/**
 * org.apache.ibatis.reflection.ParamNameResolver#getNamedParams(java.lang.Object[])
 *
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
    String findLocks(String lockName1);

    /**
     * 新增接口
     *
     * @param schedName
     * @param lockName
     */
    @TargetDataSource(value = "mysql")
    void insertLocks(String schedName, String lockName);

    @TargetDataSource("mysql")
    void delLocks(String lockName);

    /**
     * 查询接口
     */
    @TargetDataSource
    String getMysql(String password);
}
