package com.emily.infrastructure.datasource.mapper;


import com.emily.infrastructure.datasource.po.Job;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author Emily
 * @Description: Description
 * @Version: 1.0
 */
@Mapper
public interface JobMapper {
    /**
     * 查询接口
     */
    Job findJob();
}
