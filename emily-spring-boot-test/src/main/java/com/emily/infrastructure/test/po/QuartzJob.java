package com.emily.infrastructure.test.po;

/**
 * @author Emily
 * @program: spring-parent
 * @description: Mysql Quartz
 * @create: 2021/04/22
 */
public class QuartzJob {

    private String jobName;
    private String jobGroup;

    public String getJobName() {
        return jobName;
    }

    public void setJobName(String jobName) {
        this.jobName = jobName;
    }

    public String getJobGroup() {
        return jobGroup;
    }

    public void setJobGroup(String jobGroup) {
        this.jobGroup = jobGroup;
    }
}
