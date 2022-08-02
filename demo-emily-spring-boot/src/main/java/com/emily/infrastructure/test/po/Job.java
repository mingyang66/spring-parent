package com.emily.infrastructure.test.po;

import javax.validation.constraints.NotEmpty;
import java.io.Serializable;

/**
 * @author Emily
 * @Description: Description
 * @Version: 1.0
 */
public class Job implements Serializable {
    private Long id;
    private Long jobNumber;
    @NotEmpty(message = "描述不可以为空")
    private String jobDesc;
    public String a;

    public String getA() {
        return a;
    }

    public void setA(String a) {
        this.a = a;
    }

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
