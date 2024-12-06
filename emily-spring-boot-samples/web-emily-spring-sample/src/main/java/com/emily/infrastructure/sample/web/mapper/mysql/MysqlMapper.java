package com.emily.infrastructure.sample.web.mapper.mysql;


import com.emily.infrastructure.datasource.annotation.TargetDataSource;
import com.emily.infrastructure.sample.web.entity.World;
import com.emily.infrastructure.sample.web.entity.json.PubResponse;
import com.emily.infrastructure.sample.web.entity.sensitive.MapperIgnore;
import com.emily.infrastructure.sensitive.annotation.DesensitizeProperty;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * org.apache.ibatis.reflection.ParamNameResolver#getNamedParams(java.lang.Object[])
 *
 * @author Emily
 * Description
 * @since 1.0
 */
@Mapper
public interface MysqlMapper {
    /**
     * 查询接口
     */
    @TargetDataSource("mysql")
    String findLocks(String lockName1);


    @TargetDataSource("mysql")
    void delLocks(String lockName);

    /**
     * 查询接口
     */
    @TargetDataSource("mysql")
    List<World> getMysql(@DesensitizeProperty String username, String password);

    /**
     * 新增接口
     */
    @TargetDataSource(value = "mysql")
    void insertMysql(@Param("username") String username, @Param("password") String password);

    @TargetDataSource(value = "mysql")
    MapperIgnore getMapperIgnore(PubResponse response, String username, String email);
}
