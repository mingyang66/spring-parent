package com.yaomy.control.test.mapper;

import com.yaomy.control.test.po.Job;

/**
 * @Description: Description
 * @Version: 1.0
 */
public interface JobMapper {
    Job findJob();
    Boolean updateJob(Job job);
}
