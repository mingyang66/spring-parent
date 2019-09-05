package com.yaomy.control.test.serviceImpl;

import com.yaomy.control.test.mapper.JobMapper;
import com.yaomy.control.test.po.Job;
import com.yaomy.control.test.service.JobService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @Description: Description
 * @Version: 1.0
 */
@Service
public class JobServiceImpl implements JobService {
   @Autowired
    private JobMapper jobMapper;
    @Override
    public Job findJob() {
       return jobMapper.findJob();
    }
}
