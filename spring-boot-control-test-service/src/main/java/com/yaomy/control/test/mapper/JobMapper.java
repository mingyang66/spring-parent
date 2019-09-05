package com.yaomy.control.test.mapper;

import com.yaomy.control.aop.annotation.TargetDataSource;
import com.yaomy.control.test.po.Job;
import org.apache.ibatis.annotations.Mapper;

/**
 * @Description: Description
 * @Version: 1.0
 */
@Mapper
public interface JobMapper {
@TargetDataSource()
    Job findJob();
}
