package com.yaomy.control.common.control.test;

import java.io.Serializable;

/**
 * @Description: Description
 * @Version: 1.0
 */
public class School implements Serializable {
    /**
     * 学号
     */
    private Integer no;
    /**
     * name
     */
    private String name;
    /**
     * address
     */
    private String address;

    public Integer getNo() {
        return no;
    }

    public void setNo(Integer no) {
        this.no = no;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}
