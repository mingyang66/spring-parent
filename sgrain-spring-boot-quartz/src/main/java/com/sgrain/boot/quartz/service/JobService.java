package com.sgrain.boot.quartz.service;

import com.sgrain.boot.quartz.model.SgJobDetail;
import com.sgrain.boot.quartz.model.SgTrigger;

public interface JobService {
    boolean checkExists();

    void addJob(SgJobDetail sgJobDetail);

    void addTrigger(SgTrigger sgTrigger);
}
