package com.emily.infrastructure.test.mapper;


import com.emily.infrastructure.datasource.annotation.TargetDataSource;
import com.emily.infrastructure.test.po.SqlServer;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author Emily
 * @Description: Description
 * @Version: 1.0
 */
@Mapper
public interface SqlServerlMapper {
    /**
     * 查询接口
     */
    @TargetDataSource("sqlserver")
    SqlServer findSqlServer();
}
