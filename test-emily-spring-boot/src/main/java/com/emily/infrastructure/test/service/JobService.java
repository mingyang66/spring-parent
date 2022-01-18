package com.emily.infrastructure.test.service;

import com.emily.infrastructure.datasource.annotation.TargetDataSource;
import com.emily.infrastructure.test.po.Job;

public interface JobService {

    Job findJob();
}
