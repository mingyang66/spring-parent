package com.emily.boot.test.api;

import com.emily.boot.test.api.po.User;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.Map;

/**
 * @program: spring-parent
 * @description:
 * @create: 2020/12/16
 */
@RestController
public class TestController {

    @GetMapping("test1")
    public String test1(HttpServletResponse response) {
        response.setContentType("text/html");
        return "success";
    }

    @GetMapping("test2")
    public String test2(String password) {

        return "success-" + password;
    }

    @PostMapping("test3")
    public String test3(@RequestBody User user) {
        return "success" + user.getName();
    }

    @PostMapping("test4")
    public String test4(@RequestBody User user) {
        throw new RuntimeException("error");
    }

    @PostMapping("test5")
    public String test5(@RequestBody User user) {
        try {
            Thread.sleep(3000);
        } catch (Exception e) {

        }
        return "success";
    }

    @GetMapping("test6")
    public byte[] test6() {
        byte[] bytes = new byte[]{1, 2};
        return bytes;
    }

    @GetMapping("test7")
    public String test7() {
        String message = "你好，我是李焕英，哈哈哈哈哈哈哈哈哈哈或或或或或或或或或或或或或";
        String s = "";
        for (int i = 0; i < 10000; i++) {
            s = StringUtils.join(s, message);
        }
        return s;
    }

    public static void main(String[] args) {
        String message = "你好，我是李焕英，哈哈哈哈哈哈哈哈哈哈或或或或或或或或或或或或或";
        String s = "";
        for (int i = 0; i < 10000; i++) {
            s = StringUtils.join(s, message);
        }
        System.out.println(s.getBytes().length/1024);
    }
}
