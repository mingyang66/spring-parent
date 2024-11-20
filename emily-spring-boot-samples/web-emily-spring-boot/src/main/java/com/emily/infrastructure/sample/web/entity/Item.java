package com.emily.infrastructure.sample.web.entity;

/**
 * @author Emily
 * @program: spring-parent
 * 调度po
 * @since 2021/09/08
 */
public class Item {
    private String scheName;
    private String lockName;

    public String getScheName() {
        return scheName;
    }

    public void setScheName(String scheName) {
        this.scheName = scheName;
    }

    public String getLockName() {
        return lockName;
    }

    public void setLockName(String lockName) {
        this.lockName = lockName;
    }
}
