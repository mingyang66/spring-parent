package com.emily.infrastructure.test.mapper.mysql;


import com.emily.infrastructure.common.sensitive.JsonSimField;
import com.emily.infrastructure.common.sensitive.SensitiveType;
import com.emily.infrastructure.datasource.annotation.TargetDataSource;
import com.emily.infrastructure.test.po.World;
import com.emily.infrastructure.test.po.json.PubResponse;
import com.emily.infrastructure.test.po.sensitive.MapperIgnore;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

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


    @TargetDataSource("mysql")
    void delLocks(String lockName);

    /**
     * 查询接口
     */
    @TargetDataSource("mysql")
    List<World> getMysql();

    /**
     * 新增接口
     */
    @TargetDataSource(value = "mysql")
    void insertMysql(String schedName, String lockName);

    @TargetDataSource(value = "mysql")
    MapperIgnore getMapperIgnore(PubResponse response, @JsonSimField String username, @JsonSimField(SensitiveType.EMAIL) String email);
}
