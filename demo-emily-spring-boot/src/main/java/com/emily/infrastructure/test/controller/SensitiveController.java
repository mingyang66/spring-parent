package com.emily.infrastructure.test.controller;

import com.emily.infrastructure.test.po.sensitive.Person;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Description :  脱敏控制器
 * @Author :  Emily
 * @CreateDate :  Created in 2022/7/19 5:34 下午
 */
@RestController
@RequestMapping("api/sensitive")
public class SensitiveController {
    @GetMapping("/test")
    public Person test(String name) {
        Person user = new Person();
        user.setUsername("emily");
        user.setRealName(name);
        user.setPhoneNumber("19796328206");
        user.setAddress("浙江省杭州市温州市....");
        user.setIdCard("43333333333343343S");
        return user;
    }
}
