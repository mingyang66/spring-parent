package com.emily.infrastructure.sample.web.entity;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.io.Serializable;

/**
 * @author Emily
 * Description
 * @since 1.0
 */
public class Job implements Serializable {
    public String a;
    @NotNull(message = "不可为空")
    private Long id;
    private Long jobNumber;
    @NotEmpty(message = "描述不可以为空")
    private String jobDesc;

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
