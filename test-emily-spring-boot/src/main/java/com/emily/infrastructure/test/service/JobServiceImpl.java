package com.emily.infrastructure.test.service;

import com.emily.infrastructure.test.mapper.JobMapper;
import com.emily.infrastructure.test.po.Job;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @program: spring-parent
 * @description:
 * @author: Emily
 * @create: 2022/01/17
 */

@Service
public class JobServiceImpl implements JobService{
    @Autowired
    private JobMapper jobMapper;

    @Override
    public Job findJob() {
        return jobMapper.findJob();
    }
}
