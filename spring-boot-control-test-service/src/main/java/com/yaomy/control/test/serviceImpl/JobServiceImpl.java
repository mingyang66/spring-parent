package com.yaomy.control.test.serviceImpl;

import com.yaomy.control.aop.annotation.TargetDataSource;
import com.yaomy.control.test.mapper.JobMapper;
import com.yaomy.control.test.po.Job;
import com.yaomy.control.test.service.JobService;
import jdk.nashorn.internal.scripts.JO;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @Description: Description
 * @Version: 1.0
 */
@Service
public class JobServiceImpl implements JobService {
   @Autowired
    private JobMapper jobMapper;
    @Override
    @TargetDataSource("second")
    public Job findJob(String desc) {
       Job job1 = jobMapper.updateJob(StringUtils.join("测试任务222", desc));
        System.out.println(job1);
       Job job = jobMapper.findJob();
        System.out.println(job);
       return job;
    }
}
