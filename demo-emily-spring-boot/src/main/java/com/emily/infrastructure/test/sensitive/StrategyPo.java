package com.emily.infrastructure.test.sensitive;

import com.emily.infrastructure.common.sensitive.annotation.Sensitive;

/**
 * @Description :  策略
 * @Author :  Emily
 * @CreateDate :  Created in 2022/7/20 5:12 下午
 */
public class StrategyPo {
    @Sensitive
    private String name;
    @Sensitive
    private int age;
    @Sensitive
    private String address;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}
