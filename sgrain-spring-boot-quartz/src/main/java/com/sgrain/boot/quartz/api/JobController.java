package com.sgrain.boot.quartz.api;

import com.sgrain.boot.quartz.model.SgJobDetail;
import com.sgrain.boot.quartz.model.SgTrigger;
import com.sgrain.boot.quartz.service.JobService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @program: spring-parent
 * @description: 任务控制器类
 * @author: 姚明洋
 * @create: 2020/09/01
 */
@RestController
@RequestMapping("quartz")
public class JobController {
    @Autowired
    private JobService jobService;

    @GetMapping("checkExists")
    public boolean checkExists() {
        return jobService.checkExists();
    }

    @GetMapping("addJob")
    public void addJob(SgJobDetail sgJobDetail) {
        jobService.addJob(sgJobDetail);
    }

    @GetMapping("addTrigger")
    public void addTrigger(SgTrigger sgTrigger) {
        jobService.addTrigger(sgTrigger);
    }
}
