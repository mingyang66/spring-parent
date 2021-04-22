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
    QuartzJob findQuartzJob();
}
