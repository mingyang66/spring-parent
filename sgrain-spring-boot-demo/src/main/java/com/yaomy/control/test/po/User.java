package com.yaomy.control.test.po;

import com.sgrain.boot.common.po.BaseRequest;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotBlank;
import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @Description: 测试
 * @Version: 1.0
 */
public class User extends BaseRequest implements Serializable {
    @NotBlank(message = "这个姓名不能为空")
    private String name;
    @DecimalMin(value = "0", message = "年龄不可以小于0")
    private Integer age = 0;
    private Date date;


    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

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
