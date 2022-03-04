package com.emily.infrastructure.test.mapper;


import com.emily.infrastructure.datasource.annotation.TargetDataSource;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author Emily
 * @Description: Description
 * @Version: 1.0
 */
@Mapper
public interface TwoDbMapper {
    /**
     * 查询接口
     */
    @TargetDataSource("oracle")
    String getTwoDb();
}
