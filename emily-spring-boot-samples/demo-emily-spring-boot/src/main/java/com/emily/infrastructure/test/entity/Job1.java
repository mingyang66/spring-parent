package com.emily.infrastructure.test.entity;

import java.io.Serializable;
import java.util.List;

/**
 * @author Emily
 * Description
 * @since 1.0
 */
public class Job1<T> implements Serializable {
    private Long id;
    private Long jobNumber;
    private String jobDesc;
    public String a;
    private List<T> list;

    public List<T> getList() {
        return list;
    }

    public void setList(List<T> list) {
        this.list = list;
    }

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
