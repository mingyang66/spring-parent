package com.emily.sample.datasource.mapper.mysql;


import com.emily.infrastructure.datasource.annotation.TargetDataSource;
import com.emily.sample.datasource.entity.Job;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author Emily
 * Description
 * @since 1.0
 */
@TargetDataSource
@Mapper
public interface JobMapper {
    /**
     * 查询接口
     */
    Job findJob();
}
