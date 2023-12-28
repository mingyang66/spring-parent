package com.emily.infrastructure.test.plugin.valid;

import com.emily.infrastructure.autoconfigure.valid.IsInclude;
import com.emily.infrastructure.autoconfigure.valid.IsLocalTime;
import com.emily.infrastructure.date.DatePatternInfo;

/**
 * @author :  Emily
 * @since :  2023/12/24 1:41 PM
 */
public class ValidReq {
    @IsLocalTime(message = "日期格式不正确1", pattern = DatePatternInfo.HH_MM_SS, required = false)
    private String name;
    @IsInclude(includeString = {"1", "2", "3", ""}, message = "年龄不正确")
    private String age;
    @IsInclude(includeInt = {4, 5, 6, 2147483647}, message = "高度不正确")
    private int height;
    @IsInclude(includeLong = {1, 2147483648L}, message = "id不正确")
    private long id;
    @IsInclude(includeDouble = {1.0, 2.0}, message = "价格不正确")
    private double price;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }
}
