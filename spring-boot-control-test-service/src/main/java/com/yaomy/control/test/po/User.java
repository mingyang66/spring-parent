package com.yaomy.control.test.po;

import org.springframework.stereotype.Component;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotBlank;

/**
 * @Description: 测试
 * @Version: 1.0
 */
@Component
public class User {
    @NotBlank(message = "这个姓名不能为空")
    private String name;
    @DecimalMin(value = "0", message = "年龄不可以小于0")
    private Integer age;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }
}
