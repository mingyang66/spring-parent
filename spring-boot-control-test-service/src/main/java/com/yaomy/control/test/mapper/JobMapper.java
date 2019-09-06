package com.yaomy.control.test.mapper;

import com.yaomy.control.test.po.Job;
import org.apache.ibatis.annotations.Mapper;

/**
 * @Description: Description
 * @Version: 1.0
 */
@Mapper
public interface JobMapper {
    Job findJob();
    Job updateJob(String jobDesc);
}
