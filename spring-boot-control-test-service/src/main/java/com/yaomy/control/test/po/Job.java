package com.yaomy.control.test.po;

import java.io.Serializable;

/**
 * @Description: Description
 * @Version: 1.0
 */
public class Job implements Serializable {
    private Long id;
    private Long jobNumber;
    private String jobDesc;

    public String getJobDesc() {
        return jobDesc;
    }

    public void setJobDesc(String jobDesc) {
        this.jobDesc = jobDesc;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getJobNumber() {
        return jobNumber;
    }

    public void setJobNumber(Long jobNumber) {
        this.jobNumber = jobNumber;
    }
}
