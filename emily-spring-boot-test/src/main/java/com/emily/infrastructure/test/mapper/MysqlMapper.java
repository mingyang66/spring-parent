package com.emily.infrastructure.test.mapper;


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
    String findLocks(String lockName);

    /**
     * 新增接口
     * @param schedName
     * @param lockName
     */
    void insertLocks(String schedName, String lockName);

    void delLocks(String lockName);
}
