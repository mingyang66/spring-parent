package com.emily.infrastructure.test.sensitive;

import com.emily.infrastructure.test.po.sensitive.Person;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

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

    @PostMapping("/testPost")
    public Person testPost(@RequestBody SensitiveRequest request, HttpServletRequest request1, HttpServletResponse response) {
        Person user = new Person();
        user.setUsername("emily");
        user.setRealName(request.getUsername());
        user.setPhoneNumber("19796328206");
        user.setAddress("浙江省杭州市温州市....");
        user.setIdCard("43333333333343343S");
        user.setB((byte) 1);
        user.setS((short) 2);
        user.setL(12);
        user.setAge(23);
        user.setC((char) 24);
        user.setDd(23);
        user.setFl(26);
        return user;
    }

    @GetMapping("/testGet")
    public Person testGet(@RequestBody SensitiveRequest request) {
        Person user = new Person();
        user.setUsername("emily");
        user.setRealName(request.getUsername());
        user.setPhoneNumber("19796328206");
        user.setAddress("浙江省杭州市温州市....");
        user.setIdCard("43333333333343343S");
        user.setB((byte) 1);
        user.setS((short) 2);
        user.setL(12);
        user.setAge(23);
        user.setC((char) 24);
        user.setDd(23);
        user.setFl(26);
        return user;
    }
}
