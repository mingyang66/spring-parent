package com.emily.infrastructure.test.po;

/**
 * @program: spring-parent
 *  调度po
 * @author Emily
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
