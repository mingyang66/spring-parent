package com.emily.infrastructure.test.po;

/**
 * @program: spring-parent
 *  节点测试
 * @author Emily
 * @since 2021/04/22
 */
public class Node {

    private Long id;
    private String creator;
    private String mender;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCreator() {
        return creator;
    }

    public void setCreator(String creator) {
        this.creator = creator;
    }

    public String getMender() {
        return mender;
    }

    public void setMender(String mender) {
        this.mender = mender;
    }
}
