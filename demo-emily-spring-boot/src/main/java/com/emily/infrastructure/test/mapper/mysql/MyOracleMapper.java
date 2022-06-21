package com.emily.infrastructure.test.mapper.mysql;

import com.emily.infrastructure.datasource.annotation.TargetDataSource;
import com.emily.infrastructure.test.mapper.oracle.OracleMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * @Description :  子类测试
 * @Author :  Emily
 * @CreateDate :  Created in 2022/3/24 3:58 下午
 */
@Mapper
public interface MyOracleMapper extends OracleMapper {
    @TargetDataSource("mysql")
    @Override
    String getOracle();
}
